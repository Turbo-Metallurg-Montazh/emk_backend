package com.kindred.emkcrm_project_backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.db.entities.TenderFilter;
import com.kindred.emkcrm_project_backend.db.entities.UnloadingDate;
import com.kindred.emkcrm_project_backend.db.repositories.TenderFilterRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UnloadingDateRepository;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.kindred.emkcrm_project_backend.config.Constants.DATE_TIME_FORMAT;
import static com.kindred.emkcrm_project_backend.config.Constants.SCHEDULE_TIME;

@Service
public class AutomaticTendersDownloader {
    @Autowired
    FindTenders findTenders;
    @Autowired
    UnloadingDateRepository unloadingDateRepository;
    @Autowired
    TenderFilterRepository tenderFilterRepository;

    @Scheduled(cron = SCHEDULE_TIME) // запуск в 3:00 каждый день
    public void downloadNewTenders() throws JsonProcessingException {
        for (TenderFilter tenderFilter:tenderFilterRepository.findAllByActiveIs(true)) {
            long filterId = tenderFilter.getId();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
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

            FoundTendersArray foundTendersArray = findTenders.findTenders(tenderFilter.getJsonFilter(), unloadingDate.getUnloadDate().format(formatter), formattedDateTimeTo, 1, 10);
            unloadingDateRepository.save(new UnloadingDate(filterId, now));
            System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());
        }
    }
}
