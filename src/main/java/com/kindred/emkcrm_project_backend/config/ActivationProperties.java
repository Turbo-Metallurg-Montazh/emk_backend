package com.kindred.emkcrm_project_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "activation")

public record ActivationProperties(
        String key,
        long expiration_ms
) {
}
