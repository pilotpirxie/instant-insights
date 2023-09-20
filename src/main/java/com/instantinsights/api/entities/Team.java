package com.instantinsights.api.entities;

import com.instantinsights.api.dto.TeamDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Team(UUID id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Team() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static TeamDto toDto(Team team) {
        return new TeamDto(
            team.getId(),
            team.getName(),
            team.getCreatedAt(),
            team.getUpdatedAt()
        );
    }

    public static Team fromDto(TeamDto teamDto) {
        return new Team(
            teamDto.id(),
            teamDto.name(),
            teamDto.createdAt(),
            teamDto.updatedAt()
        );
    }

    public static TeamDto toDto(UserTeam userTeam) {
        return new TeamDto(
            userTeam.getTeam().getId(),
            userTeam.getTeam().getName(),
            userTeam.getTeam().getCreatedAt(),
            userTeam.getTeam().getUpdatedAt()
        );
    }
}