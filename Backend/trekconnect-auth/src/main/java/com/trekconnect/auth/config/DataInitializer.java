package com.trekconnect.auth.config;

import com.trekconnect.auth.entity.Role;
import com.trekconnect.auth.entity.UserCredentials;
import com.trekconnect.auth.repository.UserCredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Startup Data Initializer for auth_db seed accounts.
 *
 * WHY THIS CLASS WAS CREATED:
 * Seeds default demo accounts into auth_db on every application startup if they don't already exist.
 * This allows developers and testers to immediately log in with known credentials without
 * having to manually register via the UI.
 *
 * Seeded accounts:
 * - Admin:     admin@trekconnect.com     / Admin@1234    (ADMIN role)
 * - Organizer: organizer@trekconnect.com / Trek@1234     (ORGANIZER role)
 * - User:      trekker@trekconnect.com   / Trekker@1234  (USER role)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserCredentialsRepository userCredentialsRepository,
                           PasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Checking auth_db for seed demo accounts...");

        // Seed Admin account — used for Organizer approval and dispute resolution
        seedUser("admin@trekconnect.com", "Admin@1234", Role.ADMIN, "Admin");

        // Seed Organizer account — used for creating trek batches and managing events
        seedUser("organizer@trekconnect.com", "Trek@1234", Role.ORGANIZER, "Organizer");

        // Seed a default Trekker/User account — used for booking treks and submitting reviews
        seedUser("trekker@trekconnect.com", "Trekker@1234", Role.USER, "User");

        logger.info("Auth_db seed check complete.");
    }

    /**
     * Seeds a single user account if it doesn't already exist.
     * Skips insertion silently if the email is already registered.
     *
     * @param email    The unique email address of the user.
     * @param password Plain-text password which gets BCrypt-encoded before saving.
     * @param role     The role to assign (ADMIN, ORGANIZER, USER).
     * @param label    A human-readable label used in log messages.
     */
    private void seedUser(String email, String password, Role role, String label) {
        if (userCredentialsRepository.existsByEmail(email)) {
            logger.info("Demo {} account already exists: [{}]. Skipping seed.", label, email);
            return;
        }

        UserCredentials user = UserCredentials.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .isEmailVerified(true)   // Pre-verified so they can log in immediately
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        userCredentialsRepository.save(user);
        logger.info("Successfully seeded demo {} account: [{}] with role [{}]", label, email, role);
    }
}
