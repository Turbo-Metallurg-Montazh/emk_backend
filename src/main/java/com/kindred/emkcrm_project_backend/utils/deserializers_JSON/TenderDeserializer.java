package com.kindred.emkcrm_project_backend.utils.deserializers_JSON;

import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class TenderDeserializer {
    public static Tender deserialize(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Tender.class);
    }
}
