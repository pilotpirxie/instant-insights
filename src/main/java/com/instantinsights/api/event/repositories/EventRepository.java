package com.instantinsights.api.event.repositories;

import com.instantinsights.api.event.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("DELETE FROM Event e WHERE e.createdAt < :data")
    void deleteBefore(LocalDateTime data);
}