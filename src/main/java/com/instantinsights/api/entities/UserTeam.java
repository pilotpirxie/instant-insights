package com.instantinsights.api.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_teams")
public class UserTeam {
    @EmbeddedId
    private UserTeamIdComposite id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("team_id")
    @JoinColumn(name = "team_id")
    private Team team;

    public UserTeam(UserTeamIdComposite id, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt, User user, Team team) {
        this.id = id;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.team = team;
    }

    public UserTeam() {

    }

    public UserTeamIdComposite getId() {
        return id;
    }

    public void setId(UserTeamIdComposite id) {
        this.id = id;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}