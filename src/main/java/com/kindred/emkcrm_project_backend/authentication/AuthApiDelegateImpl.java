package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm.api.AuthApiDelegate;
import com.kindred.emkcrm.model.*;
import com.kindred.emkcrm_project_backend.config.EmailProperties;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.db.repositories.RoleRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import com.kindred.emkcrm_project_backend.exception.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthApiDelegateImpl implements AuthApiDelegate {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final EmailProperties emailProperties;

    public AuthApiDelegateImpl(
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

    @Override
    public ResponseEntity<TokenResponse> loginByUsername(com.kindred.emkcrm.model.LoginRequest loginRequest) {
        // Создаем адаптер для старого LoginRequest
        LoginRequest oldLoginRequest = new LoginRequest();
        oldLoginRequest.setData(loginRequest.getUsername());
        oldLoginRequest.setPassword(loginRequest.getPassword());

        User user = userService.validateUsername(oldLoginRequest);
        if (user == null) {
            throw new UnauthorizedException("Bad login or password");
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtTokenProvider.generateToken(user.getUsername()));
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TokenResponse> loginByEmail(com.kindred.emkcrm.model.LoginRequest loginRequest) {
        // Создаем адаптер для старого LoginRequest
        LoginRequest oldLoginRequest = new LoginRequest();
        oldLoginRequest.setData(loginRequest.getEmail());
        oldLoginRequest.setPassword(loginRequest.getPassword());

        User user = userService.validateEmail(oldLoginRequest);
        if (user == null) {
            throw new UnauthorizedException("Bad email or password");
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtTokenProvider.generateToken(user.getUsername()));
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MessageResponse> registerUser(UserRegistrationRequest userRegistrationRequest) {
        if (userRegistrationRequest.getUsername() == null || userRegistrationRequest.getEmail() == null 
                || userRegistrationRequest.getUsername().isBlank() || userRegistrationRequest.getEmail().isBlank()) {
            throw new BadRequestException("Email and Username are required");
        }
        
        if (!userRegistrationRequest.getEmail().substring(userRegistrationRequest.getEmail().lastIndexOf("@") + 1)
                .equals(emailProperties.domain())) {
            throw new BadRequestException("Email is not valid");
        }
        
        if (userService.findUserWithRolesByEmail(userRegistrationRequest.getEmail()) != null) {
            throw new ConflictException("Email already taken");
        }
        
        if (userService.findUserWithRolesByUsername(userRegistrationRequest.getUsername()) != null) {
            throw new ConflictException("Username already taken");
        }
        
        // Конвертируем UserRegistrationRequest в User entity
        User user = new User();
        user.setUsername(userRegistrationRequest.getUsername());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(userRegistrationRequest.getPassword());
        
        userService.encodePasswordAndSaveUser(user);
        return sendActivation(userRegistrationRequest.getEmail());
    }

    @Override
    public ResponseEntity<MessageResponse> sendActivation(String email) {
        User user = userService.findUserWithRolesByEmail(email);
        String activationToken = jwtTokenProvider.generateActivateToken(email);
        
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        
        try {
            emailService.sendActivationEmail(email, emailProperties.activation_link() + activationToken);
        } catch (MessagingException e) {
            throw new ServiceUnavailableException(String.format("Error sending activation email: %s", e));
        }
        
        MessageResponse response = new MessageResponse();
        response.setMessage("Activation sent successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MessageResponse> activateAccount(String token) {
        jwtTokenProvider.validateActivationToken(token);
        User user = userService.findUserWithRolesByEmail(jwtTokenProvider.getEmailFromActivationToken(token));
        
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        
        if (user.getRoles().contains(roleRepository.findByName("USER"))) {
            throw new ConflictException("User is already activated");
        }
        
        user.addRoles(roleRepository.findByName("USER"));
        entityManager.clear();
        userRepository.removeRolesByUserId(user.getId());
        entityManager.clear();
        userRepository.save(user);
        
        MessageResponse response = new MessageResponse();
        response.setMessage("User activated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

