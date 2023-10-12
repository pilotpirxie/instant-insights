package com.instantinsights.api.user.repositories;

import com.instantinsights.api.user.entities.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {
    PasswordRecovery findByCodeAndUserId(String code, UUID userId);
}
