package com.kindred.emkcrm_project_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.FindTenders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableScheduling
@RestController
public class EmkCrmProjectBackendApplication {
    @Autowired
    FindTenders findTenders;

    public static void main(String[] args) {
        SpringApplication.run(EmkCrmProjectBackendApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) throws JsonProcessingException {
        FoundTendersArray foundTendersArray = findTenders.findTenders(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "2011-12-30T07:43:31.681Z", "2031-12-30T07:43:31.681Z", 1, 10);
        System.out.println(foundTendersArray.getFoundTenders().getFoundTenders());
        return String.format("Hello %s!", name);

    }
}
