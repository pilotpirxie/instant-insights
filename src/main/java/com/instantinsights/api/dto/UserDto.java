package com.instantinsights.api.dto;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String password,
        String salt,
        String emailVerificationCode,
        LocalDateTime emailVerifiedAt,
        InetAddress registerIp,
        boolean isDisabled,
        String totpToken,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}