package com.instantinsights.api.services;

import java.util.UUID;

public interface AppService {
    void createApp(UUID teamId, String name);

    void deleteApp(String name);

    void disableApp(String name);

    void enableApp(String name);
}
