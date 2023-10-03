package com.instantinsights.api.user.entities;

import com.instantinsights.api.user.dto.SessionDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private User user;

    public Session(UUID id, String refreshToken, LocalDateTime createdAt, LocalDateTime updatedAt, User user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public Session() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refresh_token) {
        this.refreshToken = refresh_token;
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

    public static SessionDto toDto(Session session) {
        return new SessionDto(
            session.getId(),
            session.getRefreshToken(),
            session.getCreatedAt(),
            session.getUpdatedAt(),
            User.toDto(session.getUser())
        );
    }

    public static Session fromDto(SessionDto sessionDto) {
        return new Session(
            sessionDto.id(),
            sessionDto.refreshToken(),
            sessionDto.createdAt(),
            sessionDto.updatedAt(),
            User.fromDto(sessionDto.user())
        );
    }
}