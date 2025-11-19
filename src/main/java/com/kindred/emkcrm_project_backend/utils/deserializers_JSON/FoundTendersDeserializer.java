package com.kindred.emkcrm_project_backend.utils.deserializers_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTender;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTenders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public abstract class FoundTendersDeserializer {

    public static FoundTenders deserialize(String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        FoundTenders foundTenders = mapper.readValue(json, FoundTenders.class);

        JsonNode items = root.get("Items");
        if (items == null || !items.isArray()) {
            return foundTenders;
        }

        // гарантируем что foundTenders.foundTenders существует
        if (foundTenders.getFoundTenders() == null) {
            foundTenders.setFoundTenders(new ArrayList<>());
        }

        for (int i = 0; i < items.size(); i++) {
            JsonNode itemNode = items.get(i);

            // берём объект
            FoundTender tender = mapper.convertValue(itemNode, FoundTender.class);

            // записываем raw JSON
            tender.setOtherInformation(itemNode.toString());

            foundTenders.getFoundTenders().add(tender);
        }

        return foundTenders;
    }
}
