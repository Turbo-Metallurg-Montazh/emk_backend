package com.kindred.emkcrm_project_backend.askai;


import com.kindred.emkcrm_project_backend.model.PublicAiChatMessage;
import com.kindred.emkcrm_project_backend.model.PublicAiChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class PublicAiProxyService {

    private final RestClient restClient;
    private final String model;

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
        // Собираем “сообщения” в один промпт (самый простой и надёжный формат для CF)
        String prompt = buildPrompt(req);

        // Workers AI ожидает JSON вида { "prompt": "..." } для многих text-generation моделей
        Map<String, Object> payload = Map.of(
                "prompt", prompt
                // опционально можно добавить параметры генерации:
                // ,"max_tokens", 512
                // ,"temperature", 0.4
        );

        try {
            CloudflareAiResponse raw = restClient.post()
                    .uri("/" + model)
                    .body(payload)
                    .retrieve()
                    .body(CloudflareAiResponse.class);

            if (raw == null || raw.result == null) return "";
            // Обычно там result.response, но зависит от модели. Подстрахуемся:
            if (raw.result.response != null) return raw.result.response;
            if (raw.result.text != null) return raw.result.text;

            return "";
        } catch (Exception e) {
            throw new ResponseStatusException(
                    BAD_GATEWAY,
                    "Public AI provider request failed (Cloudflare Workers AI)",
                    e
            );
        }
    }

    private String buildPrompt(PublicAiChatRequest req) {
        StringBuilder sb = new StringBuilder();

        if (req.getSystemPrompt() != null && !req.getSystemPrompt().isBlank()) {
            sb.append("System: ").append(req.getSystemPrompt().trim()).append("\n\n");
        }

        if (req.getHistory() != null) {
            for (PublicAiChatMessage m : req.getHistory()) {
                sb.append(m.getRole().getValue()).append(": ").append(m.getContent()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("user: ").append(req.getMessage()).append("\n");
        sb.append("assistant: ");

        return sb.toString();
    }

    // ---- DTO под Cloudflare ----
    public static final class CloudflareAiResponse {
        public boolean success;
        public CloudflareAiResult result;
    }

    public static final class CloudflareAiResult {
        // встречающиеся поля (зависит от модели/эндпойнта)
        public String response;
        public String text;
    }
}