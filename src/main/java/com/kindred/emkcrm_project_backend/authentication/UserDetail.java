package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetail implements UserDetailsService {
@Autowired
private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.kindred.emkcrm_project_backend.db.entities.User user = userRepository.findByUsername(username);

        return new User(user.getUsername(), user.getPassword(), true, true, true, true, Collections.emptyList());

    }
}
