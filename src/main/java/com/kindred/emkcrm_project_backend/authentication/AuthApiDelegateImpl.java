package com.kindred.emkcrm_project_backend.authentication;

import com.kindred.emkcrm.api.AuthApiDelegate;
import com.kindred.emkcrm.model.*;
import com.kindred.emkcrm_project_backend.db.entities.User;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import com.kindred.emkcrm_project_backend.exception.ConflictException;
import com.kindred.emkcrm_project_backend.exception.NotFoundException;
import com.kindred.emkcrm_project_backend.exception.ServiceUnavailableException;
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
    public ResponseEntity<MessageResponse> registerUser(UserRegistrationRequest userRegistrationRequest) {
        // В корпоративной среде саморегистрация отключена —
        // учетные записи создаются и управляются администраторами (например, в LDAP/AD).
        MessageResponse response = new MessageResponse();
        response.setMessage("Self-registration is disabled in corporate environment. Please contact your system administrator.");
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<MessageResponse> sendActivation(String email) {
        // В корпоративной среде активация по email не используется —
        // пользователи активируются администраторами во внешней системе.
        MessageResponse response = new MessageResponse();
        response.setMessage("Account activation by email is not supported in corporate environment.");
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<MessageResponse> activateAccount(String token) {
        // Активация через токен также не используется — статусы учетных записей
        // управляются во внешней корпоративной системе.
        MessageResponse response = new MessageResponse();
        response.setMessage("Account activation via token is disabled. Please contact your system administrator.");
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }
}

