package com.example.netapp.dto.requests;

import java.time.LocalDateTime;

import com.example.netapp.entity.NotificationType;

/**
* DTO for sending notifications via websocket
* This class represents the structure of notification messages
*/

public class NotificationDTO {

    private Long notificationId;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime sentAt;
    private Long appointmentId;
    private String appointmentDetails; // Optional just in case we needed to send extra info

    public NotificationDTO() {

    }

    public NotificationDTO(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.sentAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentDetails() {
        return appointmentDetails;
    }

    public void setAppointmentDetails(String appointmentDetails) {
        this.appointmentDetails = appointmentDetails;
    }
}
