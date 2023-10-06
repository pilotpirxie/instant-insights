package com.instantinsights.api.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionDto(
    UUID id,
    String refreshToken,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UUID userId
) {
}