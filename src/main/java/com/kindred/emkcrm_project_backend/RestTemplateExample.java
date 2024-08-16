package com.kindred.emkcrm_project_backend;

import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.utils.GetTender;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class RestTemplateExample {
    public void exx() throws JsonProcessingException {

        Tender tender = GetTender.getTenderInfo("014020000452100021");
        System.out.println(tender.getUpdatedDatetime().toString());
        System.out.println(tender.getInitialSum().getPrice());
        System.out.println(tender.getId());



    }
}