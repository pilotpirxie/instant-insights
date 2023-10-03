package com.instantinsights.api.event.services;

import com.instantinsights.api.event.dto.EventDto;
import com.instantinsights.api.event.dto.EventTypeDto;
import com.instantinsights.api.event.entities.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EventService {

    void createEventType(String name, String description, Map<String, String> schema, String appName);

    void updateEventType(String name, String description, Map<String, String> schema, String appName);

    void deleteEventType(String name, String appName);

    Set<EventTypeDto> getEventTypesForApp(String name);

    void createEvent(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        EventType eventType,
        String appName
    );

    void deleteEvent(UUID id);

    List<EventDto> queryEvents(
        EventType eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName,
        int limit,
        UUID cursor,
        boolean descending
    );

    long getEventsCount(
        EventType eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName
    );
}
