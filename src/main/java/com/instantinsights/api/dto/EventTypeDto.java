package com.instantinsights.api.dto;

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
    AppDto app
) {
}
