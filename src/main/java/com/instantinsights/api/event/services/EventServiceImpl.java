package com.instantinsights.api.event.services;

import com.instantinsights.api.event.dto.EventDto;
import com.instantinsights.api.event.dto.EventTypeDto;
import com.instantinsights.api.event.entities.Event;
import com.instantinsights.api.event.entities.EventType;
import com.instantinsights.api.event.repositories.EventRepository;
import com.instantinsights.api.event.repositories.EventTypeRepository;
import com.instantinsights.api.event.repositories.QueryEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final QueryEventRepository queryEventRepository;
    private final EventTypeRepository eventTypeRepository;

    public EventServiceImpl(
        EventRepository eventRepository,
        QueryEventRepository queryEventRepository,
        EventTypeRepository eventTypeRepository
    ) {
        this.eventRepository = eventRepository;
        this.queryEventRepository = queryEventRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public void createEventType(String name, String description, Map<String, String> schema, String appName) {
        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setSchema(schema);
        eventType.setAppName(appName);
        eventTypeRepository.save(eventType);
    }

    @Override
    public void updateEventType(String name, String description, Map<String, String> schema, String appName) {
        EventType eventType = new EventType();
        eventType.setName(name);
        eventType.setDescription(description);
        eventType.setSchema(schema);
        eventType.setAppName(appName);

        eventTypeRepository.updateByNameAndAppName(name, appName, eventType);
    }

    @Override
    public void deleteEventType(String name, String appName) {
        eventTypeRepository.deleteByNameAndAppName(name, appName);
    }

    @Override
    public Set<EventTypeDto> getEventTypesForApp(String appName) {
        List<EventType> eventTypes = eventTypeRepository.findAllByAppName(appName);

        return eventTypes.stream().map(EventType::toDto).collect(Collectors.toSet());
    }

    @Override
    public void createEvent(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        EventType eventType,
        String appName
    ) {
        Event event = new Event();
        event.setId(id);
        event.setMeta(meta);
        event.setParams(params);
        event.setEventType(eventType);
        event.setAppName(appName);
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
        String appName,
        int limit,
        UUID cursor,
        boolean descending
    ) {
        List<Event> events = queryEventRepository.queryEvents(
            eventType.getName(),
            start,
            end,
            params,
            appName,
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
        String appName
    ) {
        return queryEventRepository.countEvents(
            eventType.getName(),
            start,
            end,
            params,
            appName
        );
    }
}
