package com.kindred.emkcrm_project_backend.authentication;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    // Getters and setters
    private String username;
    private String password;

}