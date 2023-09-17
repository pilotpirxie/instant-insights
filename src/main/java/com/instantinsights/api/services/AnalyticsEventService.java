package com.instantinsights.api.services;

import com.instantinsights.api.dto.EventDto;
import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.Event;
import com.instantinsights.api.entities.EventType;
import com.instantinsights.api.repositories.EventRepository;
import com.instantinsights.api.repositories.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AnalyticsEventService implements EventService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public AnalyticsEventService(EventRepository eventRepository, EventTypeRepository eventTypeRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public void createEventType(String name, String description, Map<String, String> schema, App app) {
        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setSchema(schema);
        eventType.setApp(app);
        eventTypeRepository.save(eventType);
    }

    @Override
    public void updateEventType(String name, String description, Map<String, String> schema, App app) {
        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setSchema(schema);
        eventType.setApp(app);

        eventTypeRepository.updateByNameAndApp(name, app, eventType);
    }

    @Override
    public void deleteEventType(String name, App app) {
        eventTypeRepository.deleteByNameAndApp(name, app);
    }

    @Override
    public Set<EventType> getEventTypesForApp(String name) {
        List<EventType> eventTypes = eventTypeRepository.findAllByApp(new App());

        return Set.copyOf(eventTypes);
    }

    @Override
    public void createEvent(UUID id, Map<String, String> meta, Map<String, String> params, EventType eventType, App app) {
        Event event = new Event();
        event.setId(id);
        event.setMeta(meta);
        event.setParams(params);
        event.setEventType(eventType);
        event.setApp(app);
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventDto> queryEvents(EventType eventType, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app, int limit, int offset) {
        return null;
    }

    @Override
    public int getEventsCount(EventType eventType, LocalDateTime start, LocalDateTime end, Map<String, String> params, App app) {
        return 0;
    }
}
