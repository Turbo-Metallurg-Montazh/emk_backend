package com.kindred.emkcrm_project_backend.authentication;

import lombok.Data;

@Data
public class LoginRequest {
    private String data;
    private String password;
}
