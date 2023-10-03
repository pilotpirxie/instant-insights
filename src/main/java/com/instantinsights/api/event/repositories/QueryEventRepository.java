package com.instantinsights.api.event.repositories;

import com.instantinsights.api.event.entities.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QueryEventRepository {
    List<Event> queryEvents(
        String typeName,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName,
        int limit,
        UUID cursor,
        boolean descending
    );

    long countEvents(
        String eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName
    );
}
