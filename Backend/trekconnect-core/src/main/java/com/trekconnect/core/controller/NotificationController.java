package com.trekconnect.core.controller;

import com.trekconnect.core.entity.UserNotification;
import com.trekconnect.core.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Phase 7 In-App Notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<UserNotification>> getNotifications(@AuthenticationPrincipal String userId) {
        String effectiveUser = userId != null ? userId : "usr-1";
        logger.info("REST Request: GET /api/notifications for User: {}", effectiveUser);
        return ResponseEntity.ok(notificationService.getNotifications(effectiveUser));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<UserNotification> markAsRead(@AuthenticationPrincipal String userId,
                                                        @PathVariable String id) {
        String effectiveUser = userId != null ? userId : "usr-1";
        logger.info("REST Request: PUT /api/notifications/{}/read", id);
        return ResponseEntity.ok(notificationService.markAsRead(effectiveUser, id));
    }
}
