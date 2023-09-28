package com.instantinsights.api.services;

import com.instantinsights.api.dto.TeamDto;
import com.instantinsights.api.entities.Team;
import com.instantinsights.api.repositories.TeamRepository;

import java.util.UUID;

public class TeamServiceImpl implements TeamService {
    TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public void createTeam(TeamDto teamDto) {
        Team team = Team.fromDto(teamDto);
        teamRepository.save(team);
    }

    @Override
    public void updateTeam(TeamDto teamDto) {
        Team team = Team.fromDto(teamDto);
        teamRepository.save(team);
    }

    @Override
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    @Override
    public TeamDto getTeam(UUID id) {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null) {
            return null;
        }

        return Team.toDto(team);
    }
}
