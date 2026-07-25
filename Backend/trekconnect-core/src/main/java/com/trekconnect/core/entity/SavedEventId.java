package com.trekconnect.core.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

/**
 * Composite Primary Key for SavedEvent wishlist join table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SavedEventId implements Serializable {
    private String userId;
    private String eventId;
}
