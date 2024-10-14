package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.db.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "This is an admin endpoint";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "This is a user endpoint";
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public String ownerEndpoint() {
        return "This is an owner endpoint";
    }

    @PostMapping("/login/username")
    public ResponseEntity<String> loginByUsername(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateUsername(loginRequest);
        if (user == null) {
            return new ResponseEntity<>("Bad login or password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> loginByEmail(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateEmail(loginRequest);
        if (user == null) {
            return new ResponseEntity<>("Bad email or password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtTokenProvider.generateToken(user.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User userRegistrationInfo) {
        System.out.println(userRegistrationInfo);
        if (userRegistrationInfo.getUsername() == null || userRegistrationInfo.getEmail() == null || userRegistrationInfo.getUsername().isBlank() || userRegistrationInfo.getEmail().isBlank()) {
            return new ResponseEntity<>("Email and Username are required", HttpStatus.BAD_REQUEST);
        }
        if (userService.findUserWithRolesByEmail(userRegistrationInfo.getEmail()) != null) {
            return new ResponseEntity<>("Email already taken", HttpStatus.CONFLICT);
        }

        if (userService.findUserWithRolesByUsername(userRegistrationInfo.getUsername()) != null) {
            return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);
        }

        userService.saveUser(userRegistrationInfo);
        return new ResponseEntity<>("User registered!", HttpStatus.OK);
    }

    @GetMapping("/secure-endpoint")
    public String secureEndpoint() {
        return "You have accessed a secure endpoint!";
    }

}
