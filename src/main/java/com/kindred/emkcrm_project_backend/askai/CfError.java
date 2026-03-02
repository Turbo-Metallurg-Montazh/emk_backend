package com.kindred.emkcrm_project_backend.askai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CfError(
        Integer code,
        String message
) {
}
