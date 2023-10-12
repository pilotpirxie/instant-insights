package com.instantinsights.api.app.services;

import com.instantinsights.api.app.dto.AppDto;

public interface AppService {
    void createApp(AppDto appDto);

    void deleteApp(String name);

    void disableApp(String name);

    void enableApp(String name);
}
