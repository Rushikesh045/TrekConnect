package com.trekconnect.core.listener;

import com.trekconnect.core.config.RabbitMQConfig;
import com.trekconnect.core.dto.event.UserRegisteredEvent;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Listener for user registration events.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Asynchronously receives 'user.registered' messages from auth_db service and inserts a default
 * UserProfile entity into main_db so that profile metadata is immediately ready.
 */
@Component
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserRegisteredEventListener(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTERED_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received RabbitMQ event 'user.registered' for userId [{}], email [{}]", event.getUserId(), event.getEmail());

        // Check if profile already exists to maintain idempotency
        if (userProfileRepository.existsById(event.getUserId())) {
            log.info("UserProfile for userId [{}] already exists. Skipping duplicate insert.", event.getUserId());
            return;
        }

        // Derive default display name from email username prefix (e.g. rushikesh@example.com -> rushikesh)
        String defaultName = event.getEmail().contains("@") 
                ? event.getEmail().substring(0, event.getEmail().indexOf("@"))
                : event.getEmail();

        UserProfile profile = UserProfile.builder()
                .userId(event.getUserId())
                .name(defaultName)
                .role(event.getRole() != null ? event.getRole() : "USER")
                .createdAt(event.getCreatedAt())
                .build();

        userProfileRepository.save(profile);
        log.info("Successfully persisted default UserProfile for userId [{}] in main_db", event.getUserId());
    }
}
