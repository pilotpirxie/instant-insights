package com.instantinsights.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PasswordRecoveryDto(
    UUID id,
    String code,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserDto user
) {
}
