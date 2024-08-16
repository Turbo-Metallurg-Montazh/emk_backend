package com.kindred.emkcrm_project_backend.utils.deserializers_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTenders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class FoundTendersDeserializer {
    public static FoundTenders deserialize(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FoundTenders foundTenders = mapper.readValue(json, FoundTenders.class);
        JsonNode jsonNode = mapper.readTree(json).get("Items"); // Чтение JSON в объект JsonNode
        for (int i = 0; i < jsonNode.size(); i++) {
            foundTenders.getFoundTenders().get(i).setOtherInformation(mapper.writeValueAsString(jsonNode.get(i)));
        }
        return foundTenders;
    }
}
