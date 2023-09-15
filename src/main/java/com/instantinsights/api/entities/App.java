package com.instantinsights.api.entities;

import com.instantinsights.api.utils.JsonbToMapConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "apps")
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "app")
    private Map<UUID, Event> events;

    @OneToMany(mappedBy = "app")
    private Map<UUID, EventType> eventTypes;

    @OneToMany(mappedBy = "app")
    private Set<User> users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    public App(UUID id, String name, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, Map<UUID, Event> events, Map<UUID, EventType> eventTypes, Set<User> users, Team team) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.events = events;
        this.eventTypes = eventTypes;
        this.users = users;
        this.team = team;
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

    public Map<UUID, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<UUID, Event> events) {
        this.events = events;
    }

    public Map<UUID, EventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Map<UUID, EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}