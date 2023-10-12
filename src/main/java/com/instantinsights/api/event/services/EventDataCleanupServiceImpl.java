package com.instantinsights.api.event.services;

import com.instantinsights.api.event.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventDataCleanupServiceImpl implements EventDataCleanupService {
    private final EventRepository eventRepository;

    public EventDataCleanupServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void deleteDataBefore(LocalDateTime data) {
        eventRepository.deleteBefore(data);
    }
}
