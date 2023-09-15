package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.EventType;
import com.instantinsights.api.entities.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {
}
