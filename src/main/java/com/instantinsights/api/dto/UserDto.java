package com.instantinsights.api.dto;

import com.instantinsights.api.entities.PasswordRecovery;
import com.instantinsights.api.entities.Session;
import com.instantinsights.api.entities.Team;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        LocalDateTime emailVerifiedAt,
        InetAddress registerIp,
        boolean isDisabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<TeamDto> teams,
        Set<SessionDto> sessions,
        Set<PasswordRecoveryDto> passwordRecoveries
) {
}
