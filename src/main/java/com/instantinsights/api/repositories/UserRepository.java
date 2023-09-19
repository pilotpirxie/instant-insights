package com.instantinsights.api.repositories;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u JOIN u.teams t WHERE t.id = :teamId")
    List<User> findAllByTeamId(@Param("teamId") UUID teamId);
}
