package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm_project_backend.authentication.rbac.RbacService;
import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import org.jspecify.annotations.NonNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetail implements UserDetailsService {

    private final UserRepository userRepository;
    private final RbacService rbacService;

    public UserDetail(UserRepository userRepository, RbacService rbacService) {
        this.userRepository = userRepository;
        this.rbacService = rbacService;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        com.kindred.emkcrm_project_backend.db.entities.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        Collection<GrantedAuthority> grantedAuthorities = rbacService.resolveAuthorities(username).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                grantedAuthorities
        );
    }
}
