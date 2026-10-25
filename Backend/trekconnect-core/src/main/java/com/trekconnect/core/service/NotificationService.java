package com.trekconnect.core.service;

import com.trekconnect.core.entity.UserNotification;
import com.trekconnect.core.repository.UserNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing user notifications and alerts.
 * Supports real-time WebSocket push notifications via STOMP broker.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final UserNotificationRepository notificationRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(UserNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates a new notification for a user and pushes it in real-time via WebSocket.
     */
    @Transactional
    public UserNotification createNotification(String userId, String title, String message, String type) {
        logger.info("Creating notification for User: {}, Title: {}", userId, title);

        UserNotification notification = UserNotification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type != null ? type : "GENERAL")
                .isRead(false)
                .build();

        UserNotification saved = notificationRepository.save(notification);

        // Real-time broadcast to user-specific WebSocket channel
        if (messagingTemplate != null && userId != null) {
            try {
                messagingTemplate.convertAndSend("/topic/notifications/" + userId, saved);
                logger.debug("Pushed WebSocket notification to /topic/notifications/{}", userId);
            } catch (Exception ex) {
                logger.warn("Failed to dispatch WebSocket message: {}", ex.getMessage());
            }
        }

        return saved;
    }

    /**
     * Gets all notifications for a user.
     */
    @Transactional(readOnly = true)
    public List<UserNotification> getNotifications(String userId) {
        logger.debug("Fetching notifications for User: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Marks a notification as read.
     */
    @Transactional
    public UserNotification markAsRead(String userId, String notificationId) {
        logger.info("Marking notification ID: {} as read for User: {}", notificationId, userId);

        UserNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}

