package com.kindred.emkcrm_project_backend.askai;


import com.kindred.emkcrm.model.PublicAiChatMessage;
import com.kindred.emkcrm.model.PublicAiChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Service
public class PublicAiProxyService {

    private final RestClient restClient;
    private final String model;

    public PublicAiProxyService(
            @Value("${public-ai.base-url}") String baseUrl,
            @Value("${public-ai.model}") String model,
            @Value("${public-ai.timeout-seconds:60}") long timeoutSeconds
    ) {
        this.model = model;

        Duration timeout = Duration.ofSeconds(timeoutSeconds);

        // JDK HttpClient: connect timeout
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        // Spring request factory: read timeout
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(requestFactory)
                .build();
    }

    public String ask(PublicAiChatRequest req) {
        List<OllamaMessage> msgs = new ArrayList<>();

        if (req.getSystemPrompt() != null && !req.getSystemPrompt().isBlank()) {
            msgs.add(new OllamaMessage("system", req.getSystemPrompt().trim()));
        }

        if (req.getHistory() != null) {
            for (PublicAiChatMessage m : req.getHistory()) {
                // role enum -> string
                msgs.add(new OllamaMessage(m.getRole().getValue(), m.getContent()));
            }
        }

        msgs.add(new OllamaMessage("user", req.getMessage()));

        OllamaChatRequest payload = new OllamaChatRequest(model, msgs, false);

        OllamaChatResponse raw = restClient.post()
                .uri("/api/chat")
                .body(payload)
                .retrieve()
                .body(OllamaChatResponse.class);

        if (raw == null || raw.message == null || raw.message.content == null) {
            return "";
        }
        return raw.message.content;
    }

    // ---- DTOs под Ollama ----

    public record OllamaChatRequest(
            String model,
            List<OllamaMessage> messages,
            boolean stream
    ) {
    }

    public record OllamaMessage(
            String role,
            String content
    ) {
    }

    public static final class OllamaChatResponse {
        public OllamaMessage message;
        public boolean done;
        public String model;
        public Long total_duration;
        public Long eval_count;
        public Long eval_duration;
    }
}