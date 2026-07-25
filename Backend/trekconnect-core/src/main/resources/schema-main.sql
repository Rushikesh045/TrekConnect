-- ==============================================================================
-- TREKCONNECT MAIN MONOLITH DATABASE SCHEMA (main_db)
-- Database Engine: PostgreSQL
-- Owned Exclusively By: Monolith Core Service (trekconnect-core)
-- Purpose: Holds user profiles, organizer details, treks, events, bookings,
--          payments, refund requests, reviews, saved events, and email logs.
-- ==============================================================================

-- 1. Table: user_profile
-- Purpose: Stores user profile metadata (name, phone, picture, bio, role copy).
-- Sync: User ID matches auth_db.users_credentials.id (synced asynchronously via RabbitMQ user.registered event).
CREATE TABLE IF NOT EXISTS user_profile (
    user_id VARCHAR(36) PRIMARY KEY, -- Universal UUID matching auth_db.users_credentials.id.
    name VARCHAR(150) NOT NULL, -- User's display name.
    phone VARCHAR(15) NULL, -- Optional contact phone number.
    profile_pic_url VARCHAR(500) NULL, -- Avatar picture URL.
    bio TEXT NULL, -- Optional short bio description.
    role VARCHAR(20) NOT NULL, -- Denormalized copy of user role ('USER', 'ORGANIZER', 'ADMIN') for local fast queries.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Profile creation timestamp.
    updated_at TIMESTAMP NULL -- Profile last updated timestamp.
);

-- 2. Table: organizer_details
-- Purpose: Stores organizer business verification details and status for users with role='ORGANIZER'.
CREATE TABLE IF NOT EXISTS organizer_details (
    id VARCHAR(36) PRIMARY KEY, -- Unique organizer details ID.
    user_id VARCHAR(36) NOT NULL UNIQUE, -- Foreign key referencing user_profile(user_id).
    organization_name VARCHAR(200) NOT NULL, -- Name of trekking organization or business entity.
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- Enum: 'PENDING', 'VERIFIED', 'REJECTED'.
    verification_docs_url VARCHAR(500) NULL, -- Government ID / business registration document URL.
    verified_by_admin_id VARCHAR(36) NULL, -- ID of admin who reviewed the verification request.
    verified_at TIMESTAMP NULL, -- Timestamp when organizer was verified or rejected.
    rejection_reason VARCHAR(500) NULL, -- Reason for rejection if verification failed.
    CONSTRAINT fk_organizer_user_profile FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

-- 3. Table: treks
-- Purpose: Reference catalog of forts and trekking areas (reusable across multiple event batches).
CREATE TABLE IF NOT EXISTS treks (
    id VARCHAR(36) PRIMARY KEY, -- Unique trek reference ID.
    name VARCHAR(200) NOT NULL, -- Name of fort / trekking destination (e.g. "Rajmachi Fort Trek").
    region VARCHAR(100) NULL, -- Geographic region (e.g. "Lonavala, Maharashtra").
    history TEXT NULL, -- Historical significance and background story of fort/trek.
    distance_km DECIMAL(6, 2) NULL, -- Total trek trail distance in kilometers.
    difficulty VARCHAR(20) NOT NULL, -- Enum: 'EASY', 'MODERATE', 'HARD', 'EXTREME'.
    altitude_m INT NULL, -- Altitude above sea level in meters.
    best_season VARCHAR(100) NULL, -- Recommended weather season (e.g. "Monsoon (June-Sept)").
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Record creation timestamp.
);

-- 4. Table: events
-- Purpose: Specific bookable instances/batches of a trek hosted by an organizer.
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(36) PRIMARY KEY, -- Unique event instance ID.
    trek_id VARCHAR(36) NOT NULL, -- Foreign key referencing treks(id).
    organizer_id VARCHAR(36) NOT NULL, -- Foreign key referencing organizer_details(id).
    title VARCHAR(200) NOT NULL, -- Event title (e.g. "Rajmachi Monsoon Night Trek Batch #1").
    description TEXT NULL, -- Full trip itinerary, inclusions, and exclusions.
    event_date DATE NOT NULL, -- Date of the event batch.
    price DECIMAL(10, 2) NOT NULL, -- Price per participant seat in INR.
    capacity_total INT NOT NULL, -- Maximum number of participant seats available.
    capacity_booked INT NOT NULL DEFAULT 0, -- Current count of booked participant seats.
    version INT NOT NULL DEFAULT 0, -- Optimistic locking version column preventing overbooking race conditions.
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL', -- Enum: 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED'.
    approved_by_admin_id VARCHAR(36) NULL, -- Admin ID who approved the event.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Event creation timestamp.
    CONSTRAINT fk_events_trek FOREIGN KEY (trek_id) REFERENCES treks(id) ON DELETE CASCADE,
    CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES organizer_details(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_events_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_events_status ON events(status);

-- 5. Table: event_media
-- Purpose: Photo gallery images and videos associated with an event.
CREATE TABLE IF NOT EXISTS event_media (
    id VARCHAR(36) PRIMARY KEY, -- Unique media asset ID.
    event_id VARCHAR(36) NOT NULL, -- Foreign key referencing events(id).
    media_url VARCHAR(500) NOT NULL, -- Media image or video URL.
    media_type VARCHAR(10) NOT NULL, -- Enum: 'IMAGE' or 'VIDEO'.
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Media upload timestamp.
    CONSTRAINT fk_event_media_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- 6. Table: bookings
-- Purpose: Holds participant trek bookings.
CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(36) PRIMARY KEY, -- Unique booking record ID.
    event_id VARCHAR(36) NOT NULL, -- Foreign key referencing events(id).
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing user_profile(user_id).
    num_seats INT NOT NULL DEFAULT 1, -- Number of seats reserved in this booking.
    total_amount DECIMAL(10, 2) NOT NULL, -- Total amount payable in INR.
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- Enum: 'PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED'.
    idempotency_key VARCHAR(100) NOT NULL UNIQUE, -- Idempotency key preventing duplicate booking charges on network retry.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Booking timestamp.
    CONSTRAINT fk_bookings_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_event_id ON bookings(event_id);

-- 7. Table: payments
-- Purpose: Tracks Razorpay payment transactions linked 1-to-1 with a booking.
CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) PRIMARY KEY, -- Unique payment record ID.
    booking_id VARCHAR(36) NOT NULL UNIQUE, -- Foreign key referencing bookings(id).
    razorpay_order_id VARCHAR(100) NOT NULL, -- Order ID generated by Razorpay API.
    razorpay_payment_id VARCHAR(100) NULL, -- Payment ID returned after successful checkout.
    razorpay_signature VARCHAR(255) NULL, -- HMAC-SHA256 signature for webhook verification.
    amount DECIMAL(10, 2) NOT NULL, -- Transaction amount in INR.
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED', -- Enum: 'CREATED', 'SUCCESS', 'FAILED', 'REFUNDED'.
    webhook_verified BOOLEAN NOT NULL DEFAULT FALSE, -- Flag confirming server-side webhook signature verification.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Payment creation timestamp.
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payments_razorpay_order ON payments(razorpay_order_id);

-- 8. Table: refund_requests
-- Purpose: Tracks refund requests for cancelled bookings handled by platform admins.
CREATE TABLE IF NOT EXISTS refund_requests (
    id VARCHAR(36) PRIMARY KEY, -- Unique refund request ID.
    payment_id VARCHAR(36) NOT NULL, -- Foreign key referencing payments(id).
    requested_by_user_id VARCHAR(36) NOT NULL, -- Foreign key referencing user_profile(user_id).
    reason VARCHAR(500) NULL, -- User's explanation for cancellation / refund request.
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- Enum: 'PENDING', 'APPROVED', 'REJECTED', 'PROCESSED'.
    handled_by_admin_id VARCHAR(36) NULL, -- ID of admin who approved or rejected the refund.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Request timestamp.
    CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE,
    CONSTRAINT fk_refund_user FOREIGN KEY (requested_by_user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

-- 9. Table: reviews
-- Purpose: Holds post-trek participant reviews and comments (rating 1-5, NULL rating means plain comment).
CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) PRIMARY KEY, -- Unique review record ID.
    event_id VARCHAR(36) NOT NULL, -- Foreign key referencing events(id).
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing user_profile(user_id).
    rating SMALLINT NULL, -- Star rating from 1 to 5 (NULL if plain comment).
    comment TEXT NULL, -- Review commentary / feedback text.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Submission timestamp.
    CONSTRAINT fk_reviews_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

-- 10. Table: saved_events
-- Purpose: Join table representing user wishlists/bookmarks.
CREATE TABLE IF NOT EXISTS saved_events (
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing user_profile(user_id).
    event_id VARCHAR(36) NOT NULL, -- Foreign key referencing events(id).
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Saved timestamp.
    PRIMARY KEY (user_id, event_id),
    CONSTRAINT fk_saved_user FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- 11. Table: email_log
-- Purpose: Logs outgoing notification emails sent asynchronously by the Monolith.
CREATE TABLE IF NOT EXISTS email_log (
    id VARCHAR(36) PRIMARY KEY, -- Unique email log entry ID.
    recipient_email VARCHAR(255) NOT NULL, -- Destination email address.
    type VARCHAR(50) NOT NULL, -- Enum: 'WELCOME', 'BOOKING_CONFIRMATION', 'PASSWORD_RESET', 'EVENT_APPROVED', 'REFUND_UPDATE'.
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED', -- Enum: 'QUEUED', 'SENT', 'FAILED'.
    sent_at TIMESTAMP NULL, -- Timestamp when email was successfully dispatched.
    retry_count INT NOT NULL DEFAULT 0, -- Delivery retry attempt counter.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Queue insertion timestamp.
);
