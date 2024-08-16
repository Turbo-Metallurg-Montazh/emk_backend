package com.kindred.emkcrm_project_backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AutomaticTendersDownloader {
    @Autowired
    FindTenders findTenders;
    @Scheduled(cron = "0 0 3 * * ?") // запуск в 3:00 каждый день
    public void downloadNewTenders() throws JsonProcessingException {
        FoundTendersArray foundTendersArray = findTenders.findTenders(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "2011-12-30T07:43:31.681Z", "2031-12-30T07:43:31.681Z", 1, 10);
        System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());
    }
}
