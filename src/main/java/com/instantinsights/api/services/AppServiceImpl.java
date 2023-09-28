package com.instantinsights.api.services;

import com.instantinsights.api.dto.AppDto;
import com.instantinsights.api.entities.App;
import com.instantinsights.api.repositories.AppRepository;

public class AppServiceImpl implements AppService {
    AppRepository appRepository;

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
