package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm.api.AuthApiDelegate;
import com.kindred.emkcrm.model.*;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthApiDelegateImpl implements AuthApiDelegate {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthApiDelegateImpl(
            JwtTokenProvider jwtTokenProvider,
            UserService userService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<TokenResponse> loginByUsername(LoginRequest loginRequest) {


        User user = userService.validateUsername(loginRequest);
        if (user == null) {
            throw new UnauthorizedException("Bad login or password");
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwtTokenProvider.generateToken(user.getUsername()));
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

}

