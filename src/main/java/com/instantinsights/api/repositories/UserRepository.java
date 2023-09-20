package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query(
        value = "SELECT u FROM users u JOIN users_teams ut ON u.id = ut.user_id JOIN teams t ON ut.team_id = t.id WHERE t.id = :teamId",
        nativeQuery = true
    )
    List<User> findAllByTeamId(@Param("teamId") UUID teamId);
}
