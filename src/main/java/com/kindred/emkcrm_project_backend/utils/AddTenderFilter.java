package com.kindred.emkcrm_project_backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.db.entities.TenderFilter;
import com.kindred.emkcrm_project_backend.db.repositories.TenderFilterRepository;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;
import com.kindred.emkcrm_project_backend.utils.serializers_JSON.FindTendersPostDataSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddTenderFilter {
    @Autowired
    private TenderFilterRepository tenderFilterRepository;

    public String addNewTenderFilter(String name, long userId, boolean isActive, FindTendersPost findTendersPost) throws JsonProcessingException {
        String jsonFilter = FindTendersPostDataSerializer.jsonData(findTendersPost);
        for(TenderFilter tenderFilter:tenderFilterRepository.findAllByUserId(userId)){
            if(tenderFilter.getJsonFilter().equals(jsonFilter)){
                return tenderFilter.getName();
            }
        }
        tenderFilterRepository.save(new TenderFilter(name, userId, isActive, jsonFilter));
        return "";
    }
}
