package com.kindred.emkcrm_project_backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.db.entities.UnloadingDate;
import com.kindred.emkcrm_project_backend.db.repositories.UnloadingDateRepository;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.kindred.emkcrm_project_backend.config.Constants.SCHEDULE_TIME;

@Service
public class AutomaticTendersDownloader {
    long filterId = 1;
    @Autowired
    FindTenders findTenders;
    @Autowired
    UnloadingDateRepository unloadingDateRepository;
    @Scheduled(cron = SCHEDULE_TIME) // запуск в 3:00 каждый день
    public void downloadNewTenders() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedDateTimeTo = now.format(formatter);

        UnloadingDate unloadingDate = unloadingDateRepository.findTopByFilterIdOrderByUnloadDateDesc(filterId);
        if (unloadingDate == null) {
            //LocalDateTime weekAgo = LocalDateTime.now().minusHours(168);
            LocalDateTime weekAgo = LocalDateTime.now().minusYears(5);
            unloadingDate = new UnloadingDate();
            unloadingDate.setFilterId(filterId);
            unloadingDate.setUnloadDate(weekAgo);
        }
        System.out.println(unloadingDate.getUnloadDate().format(formatter));

        FoundTendersArray foundTendersArray = findTenders.findTenders(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, unloadingDate.getUnloadDate().format(formatter), formattedDateTimeTo, 1, 10);
        unloadingDateRepository.save(new UnloadingDate(filterId, now));
        System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());
    }
}
