package com.instantinsights.api.dto;

import com.instantinsights.api.entities.UserRole;
import com.instantinsights.api.entities.UserTeamIdComposite;

import java.time.LocalDateTime;

public record UserTeamDto(
    UserTeamIdComposite id,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserDto user,
    TeamDto team
) {
}
