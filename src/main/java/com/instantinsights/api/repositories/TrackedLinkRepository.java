package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.TrackedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackedLinkRepository extends JpaRepository<TrackedLink, UUID> {
}
