package com.kindred.emkcrm_project_backend.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "public-ai")
public record PublicAiProperties(
        @Min(1) @Max(300) long timeoutSeconds,
        @NotNull @Valid CloudflareProperties cloudflare
) {
    public record CloudflareProperties(
            @NotBlank String accountId,
            @NotBlank String apiToken,
            @NotBlank String model
    ) {
    }
}
