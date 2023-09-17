package com.instantinsights.api.services;

import com.instantinsights.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostgresDataCleanupServiceService implements DataCleanupService {
    private final EventRepository eventRepository;

    @Autowired
    public PostgresDataCleanupServiceService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void deleteDataBefore(LocalDateTime data) {
        eventRepository.deleteBefore(data);
    }
}
