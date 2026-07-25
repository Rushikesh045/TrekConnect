package com.trekconnect.auth.event;

import com.trekconnect.auth.config.RabbitMQConfig;
import com.trekconnect.auth.dto.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Event publisher for user.registered RabbitMQ events.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Decouples user registration in auth_db from profile creation in main_db by publishing
 * asynchronous user.registered domain events to RabbitMQ.
 */
@Component
public class UserRegisteredEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserRegisteredEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Publishing user.registered event for userId: {}, email: {}", event.getUserId(), event.getEmail());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUTH_EXCHANGE,
                    RabbitMQConfig.USER_REGISTERED_ROUTING_KEY,
                    event
            );
            log.info("Successfully published user.registered event to RabbitMQ");
        } catch (Exception e) {
            log.error("Failed to publish user.registered event to RabbitMQ for userId [{}]: {}",
                    event.getUserId(), e.getMessage());
        }
    }
}
