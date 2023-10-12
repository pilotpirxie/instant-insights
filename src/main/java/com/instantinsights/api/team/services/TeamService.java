package com.instantinsights.api.team.services;

import com.instantinsights.api.team.dto.TeamDto;

import java.util.UUID;

public interface TeamService {
    void createTeam(TeamDto teamDto);

    void updateTeam(TeamDto teamDto);

    void deleteTeam(UUID id);

    TeamDto getTeam(UUID id);
}
