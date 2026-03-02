package com.kindred.emkcrm_project_backend.askai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        String role,
        String content
) {
}
