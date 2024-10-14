package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.db.repositories.RoleRepository;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetail implements UserDetailsService {
@Autowired
private UserRepository userRepository;
@Autowired
private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.kindred.emkcrm_project_backend.db.entities.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        user.setRoles(roleRepository.findByUsers(Set.of(user)));
        Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role.getName())))
                .collect(Collectors.toSet());
        System.out.println(grantedAuthorities);
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, grantedAuthorities);
    }
}
