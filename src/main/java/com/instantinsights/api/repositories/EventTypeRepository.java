package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.App;
import com.instantinsights.api.entities.EventType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, UUID> {
    @Transactional
    default void updateByNameAndApp(String name, App app, EventType eventType) {
        EventType existingEventType = findByNameAndApp(name, app);
        if (existingEventType != null) {
            existingEventType.setName(eventType.getName());
            existingEventType.setDescription(eventType.getDescription());
            existingEventType.setSchema(eventType.getSchema());
            existingEventType.setActive(eventType.getActive());
            existingEventType.setUpdatedAt(LocalDateTime.now());
            save(existingEventType);
        }
    }

    void deleteByNameAndApp(String name, App app);

    List<EventType> findAllByApp(App app);

    EventType findByNameAndApp(String name, App app);
}
