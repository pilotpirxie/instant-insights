package com.instantinsights.api.user.dto;

import com.instantinsights.api.team.dto.TeamDto;
import com.instantinsights.api.user.entities.UserRole;
import com.instantinsights.api.user.entities.UserTeamIdComposite;

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
