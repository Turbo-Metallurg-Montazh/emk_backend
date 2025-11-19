package com.kindred.emkcrm_project_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pagination")
public record PaginationProperties(
        int items_on_page
) {
}
