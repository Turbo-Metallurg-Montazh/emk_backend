package com.kindred.emkcrm_project_backend.utils.serializers_JSON;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;

public abstract class FindTendersPostDataSerializer {
    public static String jsonData(FindTendersPost findTendersPost) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(findTendersPost);
    }
}
