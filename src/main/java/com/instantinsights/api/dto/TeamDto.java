package com.instantinsights.api.dto;

import com.instantinsights.api.entities.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TeamDto(
        UUID id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
