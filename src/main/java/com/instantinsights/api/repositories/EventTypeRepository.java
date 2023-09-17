package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.Event;
import com.instantinsights.api.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, UUID> {
    void updateByNameAndApp(String name, App app, EventType eventType);

    void deleteByNameAndApp(String name, App app);

    List<EventType> findAllByApp(App app);
}
