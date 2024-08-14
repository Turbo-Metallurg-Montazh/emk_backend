package com.kindred.emkcrm_project_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ex {
    @Scheduled(cron = "20 9 9 * * ?") // запуск в 3:00 каждый день
    public void example() throws JsonProcessingException {
        FoundTendersArray foundTendersArray = FindTenders.findTenders(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "2011-12-30T07:43:31.681Z", "2031-12-30T07:43:31.681Z", 1, 10);
        System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());
    }
}
