package com.kindred.emkcrm_project_backend.askai;

import java.util.List;

public record CfChatRequest(
        List<CfMessage> messages
) {
}
