package com.instantinsights.api.event.repositories;

import com.instantinsights.api.event.entities.EventType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, UUID> {
    @Transactional
    default void updateByNameAndAppName(String name, String appName, EventType eventType) {
        EventType existingEventType = findByNameAndAppName(name, appName);
        if (existingEventType != null) {
            existingEventType.setName(eventType.getName());
            existingEventType.setDescription(eventType.getDescription());
            existingEventType.setSchema(eventType.getSchema());
            existingEventType.setActive(eventType.getActive());
            existingEventType.setUpdatedAt(LocalDateTime.now());
            save(existingEventType);
        }
    }

    void deleteByNameAndAppName(String name, String appName);

    List<EventType> findAllByAppName(String appName);

    EventType findByNameAndAppName(String name, String appName);
}
