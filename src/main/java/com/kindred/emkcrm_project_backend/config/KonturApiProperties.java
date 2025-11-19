package com.kindred.emkcrm_project_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external-api.kontur")
public record KonturApiProperties(
        String apiKeyHeader,
        String apiKey,
        String getTenderInfoUrl,
        String findTendersUrl
) {
}
