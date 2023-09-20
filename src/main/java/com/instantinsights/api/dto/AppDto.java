package com.instantinsights.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppDto(
    UUID id,
    String name,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    TeamDto team
) {
}
