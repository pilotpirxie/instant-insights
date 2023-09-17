package com.instantinsights.api.services;

import com.instantinsights.api.dto.EventDto;
import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EventService {

    void createEventType(String name, String description, Map<String, String> schema, App app);

    void updateEventType(String name, String description, Map<String, String> schema, App app);

    void deleteEventType(String name, App app);

    Set<EventType> getEventTypesForApp(String name);

    void createEvent(UUID id, Map<String, String> meta, Map<String, String> params, EventType eventType, App app);

    void deleteEvent(String name);

    List<EventDto> queryEvents(String name, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app, int limit, int offset);

    int getEventsCount(String name, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app);
}
