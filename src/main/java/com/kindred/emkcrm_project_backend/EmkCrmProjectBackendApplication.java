package com.kindred.emkcrm_project_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableScheduling
@RestController
public class EmkCrmProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmkCrmProjectBackendApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
