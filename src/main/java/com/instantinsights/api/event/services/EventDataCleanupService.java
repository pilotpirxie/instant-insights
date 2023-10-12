package com.instantinsights.api.event.services;

import java.time.LocalDateTime;

public interface EventDataCleanupService {
    void deleteDataBefore(LocalDateTime data);
}
