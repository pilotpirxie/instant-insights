package com.instantinsights.api.dto;

import com.instantinsights.api.entities.App;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TrackedLink(
        UUID id,
        String urlSlug,
        Boolean isActive,
        Map<String, String> redirectRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        App app
) {
}
