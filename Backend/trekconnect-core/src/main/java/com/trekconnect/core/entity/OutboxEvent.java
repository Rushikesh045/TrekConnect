package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity implementing the Transactional Outbox Pattern.
 * 
 * WHY THIS IS USED:
 * Avoids dual-write issues between RDBMS and RabbitMQ message broker.
 * Events are persisted atomically in main_db during booking state transitions
 * and published asynchronously to RabbitMQ exchange by OutboxPublisherService.
 */
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public OutboxEvent() {
    }

    public OutboxEvent(Long id, String aggregateType, String aggregateId, String eventType, String payload, String status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status != null ? status : "PENDING";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.processedAt = processedAt;
    }

    public static OutboxEventBuilder builder() {
        return new OutboxEventBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    public static class OutboxEventBuilder {
        private Long id;
        private String aggregateType;
        private String aggregateId;
        private String eventType;
        private String payload;
        private String status = "PENDING";
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public OutboxEventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OutboxEventBuilder aggregateType(String aggregateType) {
            this.aggregateType = aggregateType;
            return this;
        }

        public OutboxEventBuilder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OutboxEventBuilder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public OutboxEventBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public OutboxEventBuilder status(String status) {
            this.status = status;
            return this;
        }

        public OutboxEventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OutboxEventBuilder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public OutboxEvent build() {
            return new OutboxEvent(id, aggregateType, aggregateId, eventType, payload, status, createdAt, processedAt);
        }
    }
}
