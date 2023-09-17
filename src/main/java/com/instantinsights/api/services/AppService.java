package com.instantinsights.api.services;

public interface AppService {
    void createApp(String name, String description);

    void updateApp(String name, String description);

    void deleteApp(String name);

    void disableApp(String name);

    void enableApp(String name);
}
