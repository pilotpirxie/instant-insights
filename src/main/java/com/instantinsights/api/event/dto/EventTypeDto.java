package com.instantinsights.api.event.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record EventTypeDto(
    UUID id,
    String name, String description,
    Map<String, String> schema,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String appName
) {
}
