package com.instantinsights.api.app.services;

import com.instantinsights.api.app.dto.AppDto;
import com.instantinsights.api.app.entities.App;
import com.instantinsights.api.app.repositories.AppRepository;

public class AppServiceImpl implements AppService {
    private final AppRepository appRepository;

    public AppServiceImpl(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public void createApp(AppDto appDto) {
        App app = App.fromDto(appDto);
        appRepository.save(app);
    }

    @Override
    public void deleteApp(String name) {
        appRepository.deleteByName(name);
    }

    @Override
    public void disableApp(String name) {
        appRepository.disableByName(name);
    }

    @Override
    public void enableApp(String name) {
        appRepository.enableByName(name);
    }
}
