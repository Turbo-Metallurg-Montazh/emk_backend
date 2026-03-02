package com.kindred.emkcrm_project_backend.askai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAiChatResponse(
        List<Choice> choices
) {
}
