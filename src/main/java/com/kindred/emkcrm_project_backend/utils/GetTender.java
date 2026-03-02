package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import com.kindred.emkcrm_project_backend.exception.ServiceUnavailableException;
import com.kindred.emkcrm_project_backend.external.KonturExternalApiService;
import com.kindred.emkcrm_project_backend.utils.json.TenderJsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetTender {

    private final KonturExternalApiService konturExternalApiService;
    private final TenderJsonMapper tenderJsonMapper;

    public GetTender(
            KonturExternalApiService konturExternalApiService,
            TenderJsonMapper tenderJsonMapper
    ) {
        this.konturExternalApiService = konturExternalApiService;
        this.tenderJsonMapper = tenderJsonMapper;
    }

    public Tender getTenderInfo(String id) throws JsonProcessingException {
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Tender id must not be blank");
        }
        ResponseEntity<String> response = konturExternalApiService.getPurchaseByIdRaw(id);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().isBlank()) {
            throw new ServiceUnavailableException("Failed to get purchase details from external service");
        }
        return tenderJsonMapper.readTender(response.getBody());
    }
}
