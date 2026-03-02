package com.kindred.emkcrm_project_backend.askai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kindred.emkcrm_project_backend.config.PublicAiProperties;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import com.kindred.emkcrm_project_backend.model.PublicAiChatMessage;
import com.kindred.emkcrm_project_backend.model.PublicAiChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Slf4j
@Service
public class PublicAiProxyService {

    private static final int MAX_HISTORY_ITEMS = 50;
    private static final int MAX_PROMPT_LENGTH = 20_000;
    private static final int MAX_MESSAGE_LENGTH = 20_000;
    private static final StreamReadConstraints RESPONSE_STREAM_CONSTRAINTS = StreamReadConstraints.builder()
            .maxNestingDepth(100)
            .maxStringLength(1_000_000)
            .build();

    private final RestClient restClient;
    private final String model;
    private final ObjectReader openAiResponseReader;

    public PublicAiProxyService(
            PublicAiProperties publicAiProperties
    ) {
        Objects.requireNonNull(publicAiProperties, "publicAiProperties must not be null");
        Objects.requireNonNull(publicAiProperties.cloudflare(), "publicAiProperties.cloudflare must not be null");

        this.model = publicAiProperties.cloudflare().model();

        JsonMapper jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        jsonMapper.getFactory().setStreamReadConstraints(RESPONSE_STREAM_CONSTRAINTS);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
        this.openAiResponseReader = jsonMapper.readerFor(OpenAiChatResponse.class);

        Duration timeout = Duration.ofSeconds(publicAiProperties.timeoutSeconds());

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);

        this.restClient = RestClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4/accounts/" + publicAiProperties.cloudflare().accountId() + "/ai/run")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + publicAiProperties.cloudflare().apiToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(requestFactory)
                .build();
    }

    public String ask(PublicAiChatRequest request) {
        validateRequest(request);
        CfChatRequest payload = new CfChatRequest(buildMessages(request));

        try {
            CfChatResponse cf = restClient
                    .post()
                    .uri("/{model}", model)
                    .body(payload)
                    .retrieve()
                    .body(CfChatResponse.class);

            return extractAnswer(cf);
        } catch (RestClientResponseException e) {
            log.warn("Public AI provider returned error status: status={}, body={}", e.getStatusCode(), trimForLog(e.getResponseBodyAsString()));
            throw badGateway("Public AI provider request failed", e);
        } catch (RestClientException e) {
            log.warn("Public AI provider transport error", e);
            throw badGateway("Public AI provider request failed", e);
        } catch (JsonProcessingException e) {
            log.warn("Public AI provider returned malformed nested response", e);
            throw badGateway("Public AI provider returned malformed response", e);
        }
    }

    private String extractAnswer(CfChatResponse cf) throws JsonProcessingException {
        if (cf == null) {
            throw badGateway("Public AI provider returned empty response");
        }
        if (Boolean.FALSE.equals(cf.success())) {
            log.warn("Public AI provider returned unsuccessful response: {}", formatProviderErrors(cf.errors()));
            throw badGateway("Public AI provider returned unsuccessful response");
        }
        if (cf.result() == null || cf.result().response() == null || cf.result().response().isBlank()) {
            throw badGateway("Public AI provider returned empty answer");
        }

        OpenAiChatResponse openai = openAiResponseReader.readValue(cf.result().response());
        if (openai.choices() == null || openai.choices().isEmpty()) {
            throw badGateway("Public AI provider returned empty answer");
        }
        Choice firstChoice = openai.choices().getFirst();
        if (firstChoice == null || firstChoice.message() == null || firstChoice.message().content() == null) {
            throw badGateway("Public AI provider returned empty answer");
        }

        String content = firstChoice.message().content().trim();
        if (content.isBlank()) {
            throw badGateway("Public AI provider returned empty answer");
        }
        return content;
    }

    private List<CfMessage> buildMessages(PublicAiChatRequest request) {
        List<CfMessage> messages = new ArrayList<>();

        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            messages.add(new CfMessage("system", request.getSystemPrompt().trim()));
        }

        if (request.getHistory() != null) {
            for (PublicAiChatMessage historyMessage : request.getHistory()) {
                messages.add(new CfMessage(historyMessage.getRole().getValue(), historyMessage.getContent().trim()));
            }
        }

        messages.add(new CfMessage("user", request.getMessage().trim()));
        return messages;
    }

    private void validateRequest(PublicAiChatRequest request) {
        if (request == null) {
            throw new BadRequestException("request must not be null");
        }
        validateText(request.getMessage(), "message", MAX_MESSAGE_LENGTH);
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            validateText(request.getSystemPrompt(), "systemPrompt", MAX_PROMPT_LENGTH);
        }

        if (request.getHistory() == null) {
            return;
        }
        if (request.getHistory().size() > MAX_HISTORY_ITEMS) {
            throw new BadRequestException("history size must be <= " + MAX_HISTORY_ITEMS);
        }

        for (int i = 0; i < request.getHistory().size(); i++) {
            PublicAiChatMessage message = request.getHistory().get(i);
            if (message == null) {
                throw new BadRequestException("history[" + i + "] must not be null");
            }
            if (message.getRole() == null) {
                throw new BadRequestException("history[" + i + "].role must not be null");
            }
            validateText(message.getContent(), "history[" + i + "].content", MAX_MESSAGE_LENGTH);
        }
    }

    private void validateText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " must not be blank");
        }
        if (value.length() > maxLength) {
            throw new BadRequestException(fieldName + " is too long");
        }
    }

    private String formatProviderErrors(List<CfError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "no details";
        }
        return errors.stream()
                .limit(3)
                .map(err -> "code=" + err.code() + ", message=" + trimForLog(err.message()))
                .collect(Collectors.joining("; "));
    }

    private String trimForLog(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.replaceAll("\\s+", " ").trim();
        return trimmed.length() <= 500 ? trimmed : trimmed.substring(0, 500);
    }

    private ResponseStatusException badGateway(String message) {
        return new ResponseStatusException(BAD_GATEWAY, message);
    }

    private ResponseStatusException badGateway(String message, Throwable cause) {
        return new ResponseStatusException(BAD_GATEWAY, message, cause);
    }
}
