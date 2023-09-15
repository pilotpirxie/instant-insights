package com.instantinsights.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.UUID;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeamRepository, UUID> {
}
