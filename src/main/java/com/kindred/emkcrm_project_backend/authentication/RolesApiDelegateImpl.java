package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm.api.RolesApiDelegate;
import com.kindred.emkcrm.model.MessageResponse;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.db.repositories.RoleRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import com.kindred.emkcrm_project_backend.exception.ConflictException;
import com.kindred.emkcrm_project_backend.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class RolesApiDelegateImpl implements RolesApiDelegate {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public RolesApiDelegateImpl(
            UserService userService,
            RoleRepository roleRepository,
            UserRepository userRepository,
            EntityManager entityManager
    ) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return new ResponseEntity<>("This is an admin endpoint", HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userEndpoint() {
        return new ResponseEntity<>("This is a user endpoint", HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> ownerEndpoint() {
        return new ResponseEntity<>("This is an owner endpoint", HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MessageResponse> addAdministratorRole(String user) {
        User userEntity = userService.findUserWithRolesByUsername(user);
        if (userEntity == null) {
            throw new NotFoundException("User not found");
        }
        if (userEntity.getRoles().contains(roleRepository.findByName("ADMIN"))) {
            throw new ConflictException("User is already admin");
        }
        entityManager.clear();
        userRepository.removeRolesByUserId(userEntity.getId());
        userEntity.addRoles(roleRepository.findByName("ADMIN"));
        entityManager.clear();
        userRepository.save(userEntity);
        
        MessageResponse response = new MessageResponse();
        response.setMessage(String.format("User %s is now ADMIN", user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MessageResponse> removeAdministratorRole(String user) {
        User userEntity = userService.findUserWithRolesByUsername(user);
        if (userEntity == null) {
            throw new NotFoundException("User not found");
        }
        userEntity.removeRoles(roleRepository.findByName("ADMIN"));
        entityManager.clear();
        userRepository.removeRolesByUserId(userEntity.getId());
        entityManager.clear();
        userRepository.save(userEntity);
        
        MessageResponse response = new MessageResponse();
        response.setMessage(String.format("User %s is not now ADMIN", user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MessageResponse> deleteUser(String user) {
        User userEntity = userService.findUserWithRolesByUsername(user);
        if (userEntity == null) {
            throw new NotFoundException("User not found");
        }
        entityManager.clear();
        userRepository.removeRolesByUserId(userEntity.getId());
        entityManager.clear();
        userRepository.delete(userEntity);
        
        MessageResponse response = new MessageResponse();
        response.setMessage(String.format("User %s deleted", user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

