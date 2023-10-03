package com.instantinsights.api.user.repositories;

import com.instantinsights.api.user.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    void deleteByUserId(UUID userId);
}
