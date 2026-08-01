package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing Event batch with optimistic locking version check.
 */
public class UpdateEventRequest {

    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    private String description;

    private LocalDate eventDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private BigDecimal price;

    @Min(value = 1, message = "Capacity must be at least 1 seat")
    private Integer capacityTotal;

    private String status;

    @NotNull(message = "Version is required for optimistic concurrency control")
    private Integer version;

    public UpdateEventRequest() {
    }

    public UpdateEventRequest(String title, String description, LocalDate eventDate, BigDecimal price, Integer capacityTotal, String status, Integer version) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.price = price;
        this.capacityTotal = capacityTotal;
        this.status = status;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCapacityTotal() {
        return capacityTotal;
    }

    public void setCapacityTotal(Integer capacityTotal) {
        this.capacityTotal = capacityTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
