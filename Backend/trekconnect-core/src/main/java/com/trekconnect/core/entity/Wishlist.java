package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Wishlist entity storing trekker saved bookmarks.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Persists bookmarked treks for trekkers across user sessions.
 */
@Entity
@Table(name = "wishlists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "trek_id"})
})
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "trek_id", nullable = false)
    private String trekId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wishlist() {
    }

    public Wishlist(String id, String userId, String trekId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.trekId = trekId;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrekId() {
        return trekId;
    }

    public void setTrekId(String trekId) {
        this.trekId = trekId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static WishlistBuilder builder() {
        return new WishlistBuilder();
    }

    public static class WishlistBuilder {
        private String id;
        private String userId;
        private String trekId;
        private LocalDateTime createdAt;

        public WishlistBuilder id(String id) {
            this.id = id;
            return this;
        }

        public WishlistBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public WishlistBuilder trekId(String trekId) {
            this.trekId = trekId;
            return this;
        }

        public WishlistBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Wishlist build() {
            return new Wishlist(id, userId, trekId, createdAt);
        }
    }
}
