package com.kindred.emkcrm_project_backend.askai;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kindred.emkcrm_project_backend.model.PublicAiChatMessage;
import com.kindred.emkcrm_project_backend.model.PublicAiChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;


import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class PublicAiProxyService {

    private final RestClient restClient;
    private final String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PublicAiProxyService(
            @Value("${public-ai.cloudflare.account-id}") String accountId,
            @Value("${public-ai.cloudflare.api-token}") String apiToken,
            @Value("${public-ai.cloudflare.model}") String model,
            @Value("${public-ai.timeout-seconds:60}") long timeoutSeconds
    ) {
        this.model = model;

        Duration timeout = Duration.ofSeconds(timeoutSeconds);

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);

        this.restClient = RestClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4/accounts/" + accountId + "/ai/run")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(requestFactory)
                .build();
    }

    public String ask(PublicAiChatRequest req) {
        List<CfMessage> messages = buildMessages(req);
        CfChatRequest payload = new CfChatRequest(messages);

        try {
            CfChatResponse cf = restClient.post()
                    .uri("/" + model)
                    .body(payload)
                    .retrieve()
                    .body(CfChatResponse.class);

            if (cf == null || cf.result() == null || cf.result().response() == null)
                return "";

            // ВТОРОЙ ПАРСИНГ
            OpenAiChatResponse openai = objectMapper.readValue(
                    cf.result().response(),
                    OpenAiChatResponse.class
            );

            if (openai.choices() == null || openai.choices().isEmpty())
                return "";

            return openai.choices().getFirst().message().content();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    BAD_GATEWAY,
                    "Public AI provider request failed (Cloudflare Workers AI)",
                    e
            );
        }
    }

    private List<CfMessage> buildMessages(PublicAiChatRequest req) {
        List<CfMessage> messages = new ArrayList<>();

        // system
        if (req.getSystemPrompt() != null && !req.getSystemPrompt().isBlank()) {
            messages.add(new CfMessage("system", req.getSystemPrompt().trim()));
        }

        // history
        if (req.getHistory() != null) {
            for (PublicAiChatMessage m : req.getHistory()) {
                messages.add(new CfMessage(m.getRole().getValue(), m.getContent()));
            }
        }

        // user
        messages.add(new CfMessage("user", req.getMessage()));

        return messages;
    }
}