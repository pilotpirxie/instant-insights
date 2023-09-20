package com.instantinsights.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamDto(
    UUID id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
