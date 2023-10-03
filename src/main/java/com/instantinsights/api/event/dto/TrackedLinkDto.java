package com.instantinsights.api.event.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TrackedLinkDto(
    UUID id,
    String urlSlug,
    Boolean isActive,
    Map<String, String> redirectRules,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String appName
) {
}
