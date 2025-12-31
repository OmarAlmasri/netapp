package com.example.netapp.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.dto.requests.NotificationDTO;
import com.example.netapp.entity.NotificationType;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.services.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get all notifications for authenticated user
     * GET /api/notification
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(Authentication authentication) {
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for authenticated user
     * GET /api/notification/unread
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        Long count = notificationService.getUnreadCount(currentUser);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * Mark specific notification as read
     * PUT /api/notification/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all notifications as read
     * PUT /api/notification/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Broadcast notification to all users
     * POST /api/notification/broadcast
     * Requires ADMIN or STAFF role
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> broadcastNotification(
            @RequestBody Map<String, String> payload) {

        String title = payload.getOrDefault("title", "System Announcement");
        String message = payload.getOrDefault("message", "This is a broadcast message to all users.");

        notificationService.broadcastNotification(title, message, NotificationType.APPOINTMENT_CREATED);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Broadcast notification sent to all users"));
    }

    /**
     * Send notification to a specific user
     * POST /api/notification/send-to-user/{userId}
     * Requires ADMIN or STAFF role
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/send-to-user/{userId}")
    public ResponseEntity<Map<String, String>> sendNotificationToUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {

        String title = payload.getOrDefault("title", "Notification");
        String message = payload.getOrDefault("message", "You have a new notification.");

        notificationService.sendNotificationToUserById(userId, title, message, NotificationType.APPOINTMENT_CREATED);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Notification sent successfully"));
    }
}
