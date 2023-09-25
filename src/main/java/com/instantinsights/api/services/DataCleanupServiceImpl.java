package com.instantinsights.api.services;

import com.instantinsights.api.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataCleanupServiceImpl implements DataCleanupService {
    private final EventRepository eventRepository;

    public DataCleanupServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void deleteDataBefore(LocalDateTime data) {
        eventRepository.deleteBefore(data);
    }
}
