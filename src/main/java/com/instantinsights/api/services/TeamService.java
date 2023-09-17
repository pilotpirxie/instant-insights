package com.instantinsights.api.services;

import com.instantinsights.api.dto.TeamDto;

public interface TeamService {
    void createTeam(String name, String description);

    void updateTeam(String name, String description);

    void deleteTeam(String name);

    TeamDto getTeam(String name);
}
