package com.kindred.emkcrm_project_backend.utils.serializers_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class FindTendersJsonModifier {
    public static String findTendersJson(String json, String dateFromInstant, String dateToInstant, int fromPage) throws JsonProcessingException {

        // Преобразование JSON строки в объект JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.readValue(json, ObjectNode.class);

        // Изменение значения поля "key1"
        jsonObject.put("DateTimeFrom", dateFromInstant);
        jsonObject.put("DateTimeTo", dateToInstant);
        jsonObject.put("PageNumber", fromPage);

        // Преобразование JsonNode обратно в JSON строку
        return objectMapper.writeValueAsString(jsonObject);
    }
}
