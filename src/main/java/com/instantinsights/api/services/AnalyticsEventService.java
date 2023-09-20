package com.instantinsights.api.services;

import com.instantinsights.api.dto.EventDto;
import com.instantinsights.api.dto.EventTypeDto;
import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.Event;
import com.instantinsights.api.entities.EventType;
import com.instantinsights.api.repositories.EventRepository;
import com.instantinsights.api.repositories.EventTypeRepository;
import com.instantinsights.api.repositories.QueryEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsEventService implements EventService {
    private final EventRepository eventRepository;
    private final QueryEventRepository queryEventRepository;
    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public AnalyticsEventService(
        EventRepository eventRepository,
        QueryEventRepository queryEventRepository,
        EventTypeRepository eventTypeRepository
    ) {
        this.eventRepository = eventRepository;
        this.queryEventRepository = queryEventRepository;
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
    public Set<EventTypeDto> getEventTypesForApp(String name) {
        List<EventType> eventTypes = eventTypeRepository.findAllByApp(new App());

        return eventTypes.stream().map(EventType::toDto).collect(Collectors.toSet());
    }

    @Override
    public void createEvent(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        EventType eventType,
        App app
    ) {
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
    public List<EventDto> queryEvents(
        EventType eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        App app,
        int limit,
        UUID cursor,
        boolean descending
    ) {
        List<Event> events = queryEventRepository.queryEvents(
            eventType.getName(),
            start,
            end,
            params,
            app.getName(),
            limit,
            cursor,
            descending
        );

        return events.stream().map(Event::toDto).collect(Collectors.toList());
    }

    @Override
    public long getEventsCount(
        EventType eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        App app
    ) {
        return queryEventRepository.countEvents(
            eventType.getName(),
            start,
            end,
            params,
            app.getName()
        );
    }
}
