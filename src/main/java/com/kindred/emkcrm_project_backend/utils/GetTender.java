package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.config.KonturApiProperties;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.utils.deserializers_JSON.TenderDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GetTender {

    private final KonturApiProperties konturApiProperties;

    public GetTender(
            KonturApiProperties konturApiProperties
    ) {
        this.konturApiProperties = konturApiProperties;
    }
    public Tender getTenderInfo(String id) throws JsonProcessingException {
        String url = String.format("%s%s?", konturApiProperties.getTenderInfoUrl(), id);
        HttpHeaders headers = new HttpHeaders();
        headers.set(konturApiProperties.apiKeyHeader(), konturApiProperties.apiKey());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return TenderDeserializer.deserialize(response.getBody());
    }
}
