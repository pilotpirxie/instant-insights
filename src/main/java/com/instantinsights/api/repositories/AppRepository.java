package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppRepository extends JpaRepository<App, UUID> {

    @Query(value = "INSERT INTO apps (team_id, name, is_active) VALUES (?1, ?2, false)", nativeQuery = true)
    public void create(UUID teamId, String name);

    public void deleteByName(String name);

    @Query(value = "UPDATE apps SET is_active = false WHERE name = ?1", nativeQuery = true)
    public void disableByName(String name);

    @Query(value = "UPDATE apps SET is_active = true WHERE name = ?1", nativeQuery = true)
    public void enableByName(String name);
}
