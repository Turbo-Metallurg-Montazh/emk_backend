package com.kindred.emkcrm_project_backend;

import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import com.kindred.emkcrm_project_backend.utils.GetTender;
import com.fasterxml.jackson.core.JsonProcessingException;


public class RestTemplateExample {

    public static void main(String[] args) throws JsonProcessingException {

        Tender tender = GetTender.getTenderInfo("014020000452100021");
        System.out.println(tender.getUpdatedDatetime().toString());
        System.out.println(tender.getInitialSum().getPrice());
        System.out.println(tender.getId());



        FoundTendersArray foundTendersArray = FindTenders.findTenders(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "2011-12-30T07:43:31.681Z", "2031-12-30T07:43:31.681Z", 1, 10);
        System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());

        System.out.println(foundTendersArray.getTotalPagesCount());


    }
}