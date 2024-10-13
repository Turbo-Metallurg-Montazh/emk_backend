package com.kindred.emkcrm_project_backend.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

    @PostMapping("/generate-token")
    public String generateToken(@RequestBody String username) {
        return jwtTokenProvider.generateToken(username);
    }

    @PostMapping("/register")
    public String register(@RequestBody String json) throws IOException {
        System.out.println("Processing registration data: " + json);
        userService.createUserFromJson(json);


        return "User registered!";
    }

    @GetMapping("/secure-endpoint")
    public String secureEndpoint() {
        return "You have accessed a secure endpoint!";
    }
}
