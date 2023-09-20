package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppRepository extends JpaRepository<App, UUID> {
}
