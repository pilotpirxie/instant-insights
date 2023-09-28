package com.instantinsights.api.services;

import com.instantinsights.api.repositories.AppRepository;

import java.util.UUID;

public class AppServiceImpl implements AppService {
    AppRepository appRepository;

    @Override
    public void createApp(UUID teamId, String name) {
        appRepository.create(teamId, name);
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
