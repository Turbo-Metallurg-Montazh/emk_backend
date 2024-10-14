package com.kindred.emkcrm_project_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;
import com.kindred.emkcrm_project_backend.utils.AddTenderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableScheduling
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('USER')")
public class EmkCrmProjectBackendApplication {

    @Autowired
    private AddTenderFilter addTenderFilter;

    public static void main(String[] args) {
        SpringApplication.run(EmkCrmProjectBackendApplication.class, args);
    }

    @GetMapping("/add_tender_filter")
    public String addTenderFilter(@RequestParam(value = "name", defaultValue = "ЭМК дефолтный фильтр") String name, @RequestParam(value = "user_id", defaultValue = "0") long userId) throws JsonProcessingException {
        String answer = addTenderFilter.addNewTenderFilter(name, userId, true, new FindTendersPost(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "2011-12-30T07:43:31.681Z", "2031-12-30T07:43:31.681Z", 1));

        if (answer.isEmpty()) {
            return "Фильтр для тендеров успешно добавлен в БД";
        }
        return String.format("Фильтр уже существует под названием %s", answer);
    }
}
