package com.instantinsights.api.dto;

import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.EventType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record EventDto(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        LocalDateTime createdAt,
        EventType eventType,
        App app
) {
}
