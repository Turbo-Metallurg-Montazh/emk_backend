package com.kindred.emkcrm_project_backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.db.entities.TenderFilter;
import com.kindred.emkcrm_project_backend.db.repositories.TenderFilterRepository;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;
import com.kindred.emkcrm_project_backend.utils.json.TenderJsonMapper;
import org.springframework.stereotype.Component;

@Component
public class AddTenderFilter {
    private final TenderFilterRepository tenderFilterRepository;
    private final TenderJsonMapper tenderJsonMapper;

    public AddTenderFilter(
            TenderFilterRepository tenderFilterRepository,
            TenderJsonMapper tenderJsonMapper
    ) {
        this.tenderFilterRepository = tenderFilterRepository;
        this.tenderJsonMapper = tenderJsonMapper;
    }

    public String addNewTenderFilter(String name, long userId, boolean isActive, FindTendersPost findTendersPost) throws JsonProcessingException {
        String jsonFilter = tenderJsonMapper.serializeFilter(findTendersPost);
        for (TenderFilter tenderFilter : tenderFilterRepository.findAllByUserId(userId)) {
            if (tenderFilter.getJsonFilter().equals(jsonFilter)) {
                return tenderFilter.getName();
            }
        }
        tenderFilterRepository.save(new TenderFilter(name, userId, isActive, jsonFilter));
        return "";
    }
}
