package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.authentication.dto.AdminCreateUserRequest;
import com.kindred.emkcrm_project_backend.authentication.dto.AdminResetPasswordRequest;
import com.kindred.emkcrm_project_backend.authentication.dto.AdminUserDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
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

    @GetMapping
    public ResponseEntity<List<AdminUserDto>> listUsers() {
        List<AdminUserDto> users = userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<AdminUserDto> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(toDto(user), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AdminUserDto> createUser(@RequestBody AdminCreateUserRequest request) {
        if (request.getFirstName() == null || request.getLastName() == null ||
                request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("firstName, lastName, email and password are required");
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new ConflictException("Email already taken");
        }

        String username = usernameGenerator.generateUniqueUsername(
                request.getFirstName(),
                request.getMiddleName(),
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

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{username}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String username,
            @RequestBody AdminResetPasswordRequest request
    ) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("newPassword is required");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private AdminUserDto toDto(User user) {
        Set<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                : Set.of();
        return new AdminUserDto(user.getUsername(), user.getEmail(), roleNames);
    }
}


