package com.instantinsights.api.user.controllers;

import com.instantinsights.api.common.config.JwtConfig;
import com.instantinsights.api.common.exceptions.NotFoundException;
import com.instantinsights.api.jwt.services.JwtService;
import com.instantinsights.api.user.dto.JwtTokensDto;
import com.instantinsights.api.user.dto.LoginRequestDto;
import com.instantinsights.api.user.dto.SessionDto;
import com.instantinsights.api.user.dto.UserDto;
import com.instantinsights.api.user.exceptions.AccountServiceException;
import com.instantinsights.api.user.services.AccountService;
import com.instantinsights.api.user.services.TotpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;

@RestController
@RequestMapping({"/auth"})
public class AuthController {
    AccountService accountService;
    TotpService totpService;
    JwtService jwtService;
    JwtConfig jwtConfig;

    public AuthController(
        AccountService accountService,
        TotpService totpService,
        JwtService jwtService,
        JwtConfig jwtConfig
    ) {
        this.accountService = accountService;
        this.totpService = totpService;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/login")
    public JwtTokensDto login(@RequestBody LoginRequestDto loginRequest) throws NotFoundException, AccountServiceException {
        boolean areCredentialsValid = accountService.checkCredentials(loginRequest.email(), loginRequest.password());

        if (!areCredentialsValid) {
            throw new AccountServiceException("Invalid credentials");
        }

        UserDto user = accountService.getUserByEmail(loginRequest.email());

        if (user.isDisabled()) {
            throw new AccountServiceException("Account is disabled");
        }

        if (user.emailVerifiedAt() == null) {
            throw new AccountServiceException("Account is not verified");
        }

        if (user.totpToken() != null) {
            if (loginRequest.totpCode() == null) {
                throw new AccountServiceException("TOTP code is required");
            }
            
            boolean isTotpValid = totpService.verifyCode(user.totpToken(), loginRequest.totpCode());

            if (!isTotpValid) {
                throw new AccountServiceException("Invalid TOTP code");
            }
        }

        SessionDto sessionDto = accountService.login(user.id());
        Key accessTokenKey = jwtService.getKey(jwtConfig.getSecret());
        Key refreshTokenKey = jwtService.getKey(sessionDto.refreshToken());

        return new JwtTokensDto(
            jwtService.generateToken(user.id().toString(), accessTokenKey, jwtConfig.getAccessTokenExpiration()),
            jwtService.generateToken(user.id().toString(), refreshTokenKey, jwtConfig.getRefreshTokenExpiration())
        );
    }
}