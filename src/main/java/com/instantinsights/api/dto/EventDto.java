package com.instantinsights.api.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record EventDto(
        UUID id,
        Map<String, String> meta,
        Map<String, String> params,
        LocalDateTime createdAt,
        EventTypeDto eventType,
        AppDto app
) {
}
