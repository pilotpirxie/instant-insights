package com.instantinsights.api.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserTeamIdComposite implements Serializable {
    private UUID userId;
    private UUID teamId;

    public UserTeamIdComposite() {
    }

    public UserTeamIdComposite(UUID userId, UUID teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }
}