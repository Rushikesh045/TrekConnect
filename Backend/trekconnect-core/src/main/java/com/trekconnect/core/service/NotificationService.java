package com.trekconnect.core.service;

import com.trekconnect.core.entity.UserNotification;
import com.trekconnect.core.repository.UserNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing user notifications and alerts.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Sends in-app notifications and manages unread alert badges.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final UserNotificationRepository notificationRepository;

    @Autowired
    public NotificationService(UserNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates a new notification for a user.
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

        return notificationRepository.save(notification);
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
