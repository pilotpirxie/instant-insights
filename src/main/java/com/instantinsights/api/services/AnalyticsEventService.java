package com.instantinsights.api.services;

import com.instantinsights.api.dto.EventDto;
import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AnalyticsEventService implements EventService {

    @Override
    public void createEventType(String name, String description, Map<String, String> schema, App app) {

    }

    @Override
    public void updateEventType(String name, String description, Map<String, String> schema, App app) {

    }

    @Override
    public void deleteEventType(String name, App app) {

    }

    @Override
    public Set<EventType> getEventTypesForApp(String name) {
        return null;
    }

    @Override
    public void createEvent(UUID id, Map<String, String> meta, Map<String, String> params, EventType eventType, App app) {

    }

    @Override
    public void deleteEvent(String name) {

    }

    @Override
    public List<EventDto> queryEvents(String name, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app, int limit, int offset) {
        return null;
    }

    @Override
    public int getEventsCount(String name, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app) {
        return 0;
    }
}
