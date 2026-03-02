package com.kindred.emkcrm_project_backend.askai;

import com.kindred.emkcrm_project_backend.api.PublicAiApiDelegate;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import com.kindred.emkcrm_project_backend.model.PublicAiChatRequest;
import com.kindred.emkcrm_project_backend.model.PublicAiChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PublicAiApiDelegateImpl implements PublicAiApiDelegate {

    private final PublicAiProxyService proxyService;

    public PublicAiApiDelegateImpl(PublicAiProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public ResponseEntity<PublicAiChatResponse> publicAiChat(PublicAiChatRequest request) {
        if (request == null) {
            throw new BadRequestException("request must not be null");
        }
        String answer = proxyService.ask(request);
        return ResponseEntity.ok(new PublicAiChatResponse(answer));
    }
}
