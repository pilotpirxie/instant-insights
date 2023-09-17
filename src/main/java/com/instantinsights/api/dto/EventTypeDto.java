package com.instantinsights.api.dto;

import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.Event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record EventTypeDto(
        UUID id,
        String name, String description,
        Map<String, String> schema,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        App app,
        Set<Event> events
) {
}
