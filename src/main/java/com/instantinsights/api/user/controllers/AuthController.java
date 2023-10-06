package com.instantinsights.api.user.controllers;

import com.instantinsights.api.common.config.JwtConfig;
import com.instantinsights.api.common.exceptions.BadRequestHttpException;
import com.instantinsights.api.common.exceptions.NotFoundHttpException;
import com.instantinsights.api.jwt.services.JwtService;
import com.instantinsights.api.user.dto.*;
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

    @PostMapping("/totp-status")
    public TotpStatusResponseDto totpStatus(@RequestBody TotpStatusRequestDto totpStatusRequest) throws NotFoundHttpException {
        UserDto user = accountService.getUserByEmail(totpStatusRequest.email());

        if (user == null) {
            throw new NotFoundHttpException("User not found");
        }

        return new TotpStatusResponseDto(user.totpToken() != null);
    }

    @PostMapping("/login")
    public JwtTokensResponseDto login(@RequestBody LoginRequestDto loginRequest) throws NotFoundHttpException, AccountServiceException, BadRequestHttpException {
        boolean areCredentialsValid = accountService.checkCredentials(loginRequest.email(), loginRequest.password());

        if (!areCredentialsValid) {
            throw new BadRequestHttpException("Invalid credentials");
        }

        UserDto user = accountService.getUserByEmail(loginRequest.email());

        if (user.isDisabled()) {
            throw new BadRequestHttpException("Account is disabled");
        }

        if (user.emailVerifiedAt() == null) {
            throw new BadRequestHttpException("Account is not verified");
        }

        if (user.totpToken() != null) {
            if (loginRequest.totpCode() == null) {
                throw new BadRequestHttpException("TOTP code is required");
            }

            boolean isTotpValid = totpService.verifyCode(user.totpToken(), loginRequest.totpCode());

            if (!isTotpValid) {
                throw new BadRequestHttpException("Invalid TOTP code");
            }
        }

        SessionDto sessionDto = accountService.login(user.id());
        Key accessTokenKey = jwtService.getKey(jwtConfig.getSecret());
        Key refreshTokenKey = jwtService.getKey(sessionDto.refreshToken());

        return new JwtTokensResponseDto(
            jwtService.generateToken(user.id().toString(),
                                     accessTokenKey,
                                     jwtConfig.getAccessTokenExpiration(),
                                     sessionDto.id().toString()),
            jwtService.generateToken(user.id().toString(),
                                     refreshTokenKey,
                                     jwtConfig.getRefreshTokenExpiration(),
                                     sessionDto.id().toString())
        );
    }

//    @PostMapping("/refresh")
//    public JwtTokensResponseDto refresh(@RequestBody JwtRefreshRequestDto jwtRefreshRequestDto) throws AccountServiceException {
//        Claims claims = jwtService.getClaims(jwtRefreshRequestDto.refreshToken());
//        String sessionId = claims.getId();
//        String userId = claims.getSubject();
//
//        UserDto user = accountService.getUserById(userId);
//        SessionDto sessionDto = accountService.getSessionById(sessionId);
//
//        if (sessionDto == null) {
//            throw new AccountServiceException("Session not found");
//        }
//
//        if (!sessionDto.userId().toString().equals(userId)) {
//            throw new AccountServiceException("Invalid refresh token");
//        }
//
//        Key refreshTokenKey = jwtService.getKey(sessionDto.refreshToken());
//        boolean isValid = jwtService.validateToken(jwtRefreshRequestDto.refreshToken(), refreshTokenKey);
//
//        if (!isValid) {
//            throw new AccountServiceException("Invalid refresh token");
//        }
//
//        if (user.isDisabled()) {
//            throw new AccountServiceException("Account is disabled");
//        }
//
//        if (user.emailVerifiedAt() == null) {
//            throw new AccountServiceException("Account is not verified");
//        }
//
//        SessionDto newSessionDto = accountService.refresh(userId, sessionId);
//        Key accessTokenKey = jwtService.getKey(jwtConfig.getSecret());
//        Key newRefreshTokenKey = jwtService.getKey(newSessionDto.refreshToken());
//
//        return new JwtTokensResponseDto(
//            jwtService.generateToken(user.id().toString(), accessTokenKey, jwtConfig.getAccessTokenExpiration()),
//            jwtService.generateToken(user.id().toString(), newRefreshTokenKey, jwtConfig.getRefreshTokenExpiration())
//        );
//    }
}