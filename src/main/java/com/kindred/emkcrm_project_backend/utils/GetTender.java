package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.utils.deserializers_JSON.TenderDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.kindred.emkcrm_project_backend.config.Constants.*;

public abstract class GetTender {
    public static Tender getTenderInfo(String id) throws JsonProcessingException {
        String url = String.format("%s%s?", GET_TENDER_INFO_URL, id);
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return TenderDeserializer.deserialize(response.getBody());
    }
}
