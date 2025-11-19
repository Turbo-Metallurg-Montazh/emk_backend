package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.config.EmailProperties;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.db.repositories.RoleRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final EmailProperties emailProperties;

    public AuthController(
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            EmailService emailService,
            RoleRepository roleRepository,
            UserRepository userRepository,
            EntityManager entityManager,
            EmailProperties emailProperties
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.emailProperties = emailProperties;
    }


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
    public ResponseEntity<Map<String, String>> loginByUsername(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateUsername(loginRequest);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad login or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("token", jwtTokenProvider.generateToken(user.getUsername()));
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping("/login/email")
    public ResponseEntity<Map<String, String>> loginByEmail(@RequestBody LoginRequest loginRequest) {
        User user = userService.validateEmail(loginRequest);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad email or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("token", jwtTokenProvider.generateToken(user.getUsername()));
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User userRegistrationInfo) {
        Map<String, String> response = new HashMap<>();
        if (userRegistrationInfo.getUsername() == null || userRegistrationInfo.getEmail() == null || userRegistrationInfo.getUsername().isBlank() || userRegistrationInfo.getEmail().isBlank()) {
            response.put("error", "Email and Username are required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userRegistrationInfo.getEmail().substring(userRegistrationInfo.getEmail().lastIndexOf("@") + 1).equals(emailProperties.domain())) {
            response.put("error", "Email is not valid");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userService.findUserWithRolesByEmail(userRegistrationInfo.getEmail()) != null) {
            response.put("error", "Email already taken");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (userService.findUserWithRolesByUsername(userRegistrationInfo.getUsername()) != null) {
            response.put("error", "Username already taken");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        userService.encodePasswordAndSaveUser(userRegistrationInfo);
        return sendActivation(userRegistrationInfo.getEmail());
    }

    @GetMapping("/send-activation")
    public ResponseEntity<Map<String, String>> sendActivation(@RequestParam("email") String email) {
        User user = userService.findUserWithRolesByEmail(email);
        String activationToken = jwtTokenProvider.generateActivateToken(email);
        Map<String, String> response = new HashMap<>();
        if (user == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        try {
            emailService.sendActivationEmail(email, emailProperties.activation_link() + activationToken);
        } catch (MessagingException e) {
            response.put("error", String.format("Error sending activation email: %s", e));
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
        response.put("message", "Activation sent successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/activate")
    public ResponseEntity<Map<String, String>> activateAccount(@RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();
        jwtTokenProvider.validateActivationToken(token);
        User user = userService.findUserWithRolesByEmail(jwtTokenProvider.getEmailFromActivationToken(token));
        if (user == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (user.getRoles().contains(roleRepository.findByName("USER"))) {
            response.put("error", "User is already activated");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        user.addRoles(roleRepository.findByName("USER"));
        entityManager.clear();
        userRepository.removeRolesByUserId(user.getId());
        entityManager.clear();
        userRepository.save(user);
        response.put("message", "User activated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/add-administrator-role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> addAdministratorRole(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        Map<String, String> response = new HashMap<>();
        if (user == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (user.getRoles().contains(roleRepository.findByName("ADMIN"))) {
            response.put("error", "User is already admin");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        entityManager.clear();
        userRepository.removeRolesByUserId(user.getId());
        user.addRoles(roleRepository.findByName("ADMIN"));
        entityManager.clear();
        userRepository.save(user);
        response.put("message", String.format("User %s is now ADMIN", userName));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/remove-administrator-role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> removeAdministratorRole(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        Map<String, String> response = new HashMap<>();
        if (user == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        user.removeRoles(roleRepository.findByName("ADMIN"));
        entityManager.clear();
        userRepository.removeRolesByUserId(user.getId());
        entityManager.clear();
        userRepository.save(user);
        response.put("message", String.format("User %s is not now ADMIN", userName));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/delete-user")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam("user") String userName) {
        User user = userService.findUserWithRolesByUsername(userName);
        Map<String, String> response = new HashMap<>();
        if (user == null) {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        entityManager.clear();
        userRepository.removeRolesByUserId(user.getId());
        entityManager.clear();
        userRepository.delete(user);
        response.put("message", String.format("User %s deleted", userName));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}