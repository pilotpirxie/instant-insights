package com.instantinsights.api.dto;

import com.instantinsights.api.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record PasswordRecoveryDto(
        UUID id,
        String code,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
