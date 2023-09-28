package com.instantinsights.api.services;

import com.instantinsights.api.dto.AppDto;

public interface AppService {
    void createApp(AppDto appDto);

    void deleteApp(String name);

    void disableApp(String name);

    void enableApp(String name);
}
