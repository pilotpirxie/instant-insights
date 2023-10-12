package com.instantinsights.api.user.repositories;

import com.instantinsights.api.user.entities.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, UUID> {
}
