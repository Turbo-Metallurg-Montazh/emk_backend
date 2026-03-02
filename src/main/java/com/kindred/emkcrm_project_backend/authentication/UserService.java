package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.model.LoginRequest;
import com.kindred.emkcrm_project_backend.authentication.rbac.RbacService;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import com.kindred.emkcrm_project_backend.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RbacService rbacService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RbacService rbacService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rbacService = rbacService;
        this.passwordEncoder = passwordEncoder;
    }


    public void encodePasswordAndSaveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    public User validateUsername(LoginRequest loginInfo) {
        User user = findUserWithRolesByUsername(loginInfo.getUsername());
        if (user != null && passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())){
            return user;
        }
        throw new UnauthorizedException("Bad login or password");
    }


    public User findUserWithRolesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        user.setRoles(rbacService.findRolesByUsername(username));
        return user;
    }
}
