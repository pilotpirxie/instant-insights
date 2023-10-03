package com.instantinsights.api.event.entities;

import com.instantinsights.api.event.dto.EventDto;
import com.instantinsights.api.utils.JsonbToMapConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "meta", nullable = false)
    @Convert(converter = JsonbToMapConverter.class)
    private Map<String, String> meta;

    @Column(name = "params", nullable = false)
    @Convert(converter = JsonbToMapConverter.class)
    private Map<String, String> params;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_name", referencedColumnName = "name", insertable = false, updatable = false)
    private EventType eventType;

    @Column(name = "app_name", nullable = false)
    private String appName;

    public Event(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        LocalDateTime createdAt,
        EventType eventType,
        String appName
    ) {
        this.id = id;
        this.meta = meta;
        this.params = params;
        this.createdAt = createdAt;
        this.eventType = eventType;
        this.appName = appName;
    }

    public Event() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static EventDto toDto(Event event) {
        return new EventDto(
            event.getId(),
            event.getMeta(),
            event.getParams(),
            event.getCreatedAt(),
            EventType.toDto(event.getEventType()),
            event.getAppName()
        );
    }

    public static Event fromDto(EventDto eventDto) {
        return new Event(
            eventDto.id(),
            eventDto.meta(),
            eventDto.params(),
            eventDto.createdAt(),
            EventType.fromDto(eventDto.eventType()),
            eventDto.appName()
        );
    }
}