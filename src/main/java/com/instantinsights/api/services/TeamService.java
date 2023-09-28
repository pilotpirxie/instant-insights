package com.instantinsights.api.services;

import com.instantinsights.api.dto.TeamDto;

import java.util.UUID;

public interface TeamService {
    void createTeam(TeamDto teamDto);

    void updateTeam(TeamDto teamDto);

    void deleteTeam(UUID id);

    TeamDto getTeam(UUID id);
}
