package com.kindred.emkcrm_project_backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.authentication.UserService;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;
import com.kindred.emkcrm_project_backend.exception.UnauthorizedException;
import com.kindred.emkcrm_project_backend.utils.AddTenderFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableScheduling
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('USER')")
public class EmkCrmProjectBackendApplication {

    private final AddTenderFilter addTenderFilter;
    private final UserService userService;

    public EmkCrmProjectBackendApplication(
            AddTenderFilter addTenderFilter,
            UserService userService
    ) {
        this.addTenderFilter = addTenderFilter;
        this.userService = userService;
    }


    static void main(String[] args) {
        SpringApplication.run(EmkCrmProjectBackendApplication.class, args);

    }

    @GetMapping("/add_tender_filter")
    public String addTenderFilter(
            @RequestParam(value = "name", defaultValue = "ЭМК дефолтный фильтр") String name,
            Authentication authentication
    ) throws JsonProcessingException {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new UnauthorizedException("Unauthorized");
        }

        User user = userService.findUserWithRolesByUsername(authentication.getName());
        if (user == null || user.getId() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        String answer = addTenderFilter.addNewTenderFilter(
                name,
                user.getId(),
                true,
                new FindTendersPost(
                        null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null,
                        "2011-12-30T07:43:31.681Z",
                        "2031-12-30T07:43:31.681Z",
                        1
                )
        );

        if (answer.isEmpty()) {
            return "Фильтр для тендеров успешно добавлен в БД";
        }
        return String.format("Фильтр уже существует под названием %s", answer);
    }

}
