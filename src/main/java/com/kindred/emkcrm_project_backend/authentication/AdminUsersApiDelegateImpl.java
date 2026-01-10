package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm.api.AdminUsersApiDelegate;
import com.kindred.emkcrm.model.AdminCreateUserRequest;
import com.kindred.emkcrm.model.AdminResetPasswordRequest;
import com.kindred.emkcrm.model.AdminUserDto;
import com.kindred.emkcrm.model.MessageResponse;
import com.kindred.emkcrm_project_backend.db.entities.Role;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.db.repositories.RoleRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import com.kindred.emkcrm_project_backend.exception.ConflictException;
import com.kindred.emkcrm_project_backend.exception.NotFoundException;
import com.kindred.emkcrm_project_backend.utils.UsernameGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUsersApiDelegateImpl implements AdminUsersApiDelegate {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;

    public AdminUsersApiDelegateImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UsernameGenerator usernameGenerator,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDto>> listUsers() {
        List<AdminUserDto> users = userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDto> getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(toDto(user), HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDto> createUser(AdminCreateUserRequest request) {
        if (request.getFirstName() == null || request.getLastName() == null ||
                request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("firstName, lastName, email and password are required");
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new ConflictException("Email already taken");
        }

        String username = usernameGenerator.generateUniqueUsername(
                request.getFirstName(),
                request.getMiddleName().orElse(""),
                request.getLastName()
        );

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleRepository::findByName)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        userRepository.delete(user);
        
        MessageResponse response = new MessageResponse();
        response.setMessage(String.format("User %s deleted", username));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> resetUserPassword(String username, AdminResetPasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("newPassword is required");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        MessageResponse response = new MessageResponse();
        response.setMessage(String.format("Password for user %s has been reset", username));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private AdminUserDto toDto(User user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                : List.of();
        return new AdminUserDto()
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames);
    }
}

