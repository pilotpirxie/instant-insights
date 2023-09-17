package com.instantinsights.api.services;

import java.time.LocalDateTime;

public interface DataCleanupService {
    void deleteDataBefore(LocalDateTime data);
}
