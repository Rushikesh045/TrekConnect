package com.trekconnect.core.service;

import com.trekconnect.core.entity.OutboxEvent;
import com.trekconnect.core.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that publishes pending outbox events to RabbitMQ.
 */
@Service
@EnableScheduling
public class OutboxPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxPublisherService.class);

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OutboxPublisherService(OutboxEventRepository outboxEventRepository,
                                  @Autowired(required = false) RabbitTemplate rabbitTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Polls pending outbox events every 5 seconds and publishes them.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc("PENDING");
        if (pendingEvents.isEmpty()) {
            return;
        }

        logger.info("Found {} pending outbox event(s) to publish", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                if (rabbitTemplate != null) {
                    String routingKey = "trek.event." + event.getEventType().toLowerCase();
                    rabbitTemplate.convertAndSend("trekconnect.exchange", routingKey, event.getPayload());
                    logger.info("Published outbox event ID: {} [{}] to RabbitMQ with routing key: {}", 
                                event.getId(), event.getEventType(), routingKey);
                } else {
                    logger.info("RabbitTemplate not configured. Simulated event publish for outbox event ID: {} [{}]", 
                                event.getId(), event.getEventType());
                }

                event.setStatus("PROCESSED");
                event.setProcessedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
            } catch (Exception e) {
                logger.error("Failed to publish outbox event ID {}: {}", event.getId(), e.getMessage());
                event.setStatus("FAILED");
                outboxEventRepository.save(event);
            }
        }
    }
}

