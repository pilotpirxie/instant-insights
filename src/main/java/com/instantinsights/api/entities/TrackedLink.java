package com.instantinsights.api.entities;

import com.instantinsights.api.dto.TrackedLinkDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_name", referencedColumnName = "name", insertable = false, updatable = false)
    private App app;

    public TrackedLink(
        UUID id,
        String urlSlug,
        Boolean isActive,
        Map<String, String> redirectRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        App app
    ) {
        this.id = id;
        this.urlSlug = urlSlug;
        this.isActive = isActive;
        this.redirectRules = redirectRules;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.app = app;
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

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public static TrackedLinkDto toDto(TrackedLink trackedLink) {
        return new TrackedLinkDto(
            trackedLink.getId(),
            trackedLink.getUrlSlug(),
            trackedLink.getActive(),
            trackedLink.getRedirectRules(),
            trackedLink.getCreatedAt(),
            trackedLink.getUpdatedAt(),
            App.toDto(trackedLink.getApp())
        );
    }
}