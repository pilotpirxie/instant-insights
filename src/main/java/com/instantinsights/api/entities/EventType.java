package com.instantinsights.api.entities;

import com.instantinsights.api.utils.JsonbToMapConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "event_types")
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "schema", nullable = false)
    @Convert(converter = JsonbToMapConverter.class)
    private Map<String, String> schema;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_name", insertable = false, updatable = false)
    private App app;

    @OneToMany(mappedBy = "eventType")
    private Map<UUID, Event> events;

    public EventType(UUID id, String name, String description, Map<String, String> schema, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, App app, Map<UUID, Event> events) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.schema = schema;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.app = app;
        this.events = events;
    }

    public EventType() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, String> schema) {
        this.schema = schema;
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

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Map<UUID, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<UUID, Event> events) {
        this.events = events;
    }
}