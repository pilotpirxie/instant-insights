package com.instantinsights.api.event.entities;

import com.instantinsights.api.event.dto.TrackedLinkDto;
import com.instantinsights.api.utils.JsonbToMapConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tracked_links")
public class TrackedLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "url_slug", nullable = false)
    private String urlSlug;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "redirect_rules", nullable = false)
    @Convert(converter = JsonbToMapConverter.class)
    private Map<String, String> redirectRules;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "app_name", nullable = false)
    private String appName;

    public TrackedLink(
        UUID id,
        String urlSlug,
        Boolean isActive,
        Map<String, String> redirectRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String appName
    ) {
        this.id = id;
        this.urlSlug = urlSlug;
        this.isActive = isActive;
        this.redirectRules = redirectRules;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.appName = appName;
    }

    public TrackedLink() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrlSlug() {
        return urlSlug;
    }

    public void setUrlSlug(String urlSlug) {
        this.urlSlug = urlSlug;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Map<String, String> getRedirectRules() {
        return redirectRules;
    }

    public void setRedirectRules(Map<String, String> redirectRules) {
        this.redirectRules = redirectRules;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static TrackedLinkDto toDto(TrackedLink trackedLink) {
        return new TrackedLinkDto(
            trackedLink.getId(),
            trackedLink.getUrlSlug(),
            trackedLink.getActive(),
            trackedLink.getRedirectRules(),
            trackedLink.getCreatedAt(),
            trackedLink.getUpdatedAt(),
            trackedLink.getAppName()
        );
    }

    public static TrackedLink fromDto(TrackedLinkDto trackedLinkDto) {
        return new TrackedLink(
            trackedLinkDto.id(),
            trackedLinkDto.urlSlug(),
            trackedLinkDto.isActive(),
            trackedLinkDto.redirectRules(),
            trackedLinkDto.createdAt(),
            trackedLinkDto.updatedAt(),
            trackedLinkDto.appName()
        );
    }
}