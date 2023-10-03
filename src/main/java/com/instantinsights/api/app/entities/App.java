package com.instantinsights.api.app.entities;

import com.instantinsights.api.app.dto.AppDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "apps")
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    public App(
        UUID id,
        String name,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID teamId
    ) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.teamId = teamId;
    }

    public App() {
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public static AppDto toDto(App app) {
        return new AppDto(
            app.getId(),
            app.getName(),
            app.getActive(),
            app.getCreatedAt(),
            app.getUpdatedAt(),
            app.getTeamId()
        );
    }

    public static App fromDto(AppDto appDto) {
        return new App(
            appDto.id(),
            appDto.name(),
            appDto.isActive(),
            appDto.createdAt(),
            appDto.updatedAt(),
            appDto.teamId()
        );
    }
}