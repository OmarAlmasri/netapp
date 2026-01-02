package com.example.netapp.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.netapp.dto.requests.NotificationDTO;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.NotificationEntity;
import com.example.netapp.entity.NotificationType;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.entity.UserRole;
import com.example.netapp.repository.NotificationRepository;
import com.example.netapp.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Async
    public void sendNotificationToUser(UserEntity user, String title, String message, NotificationType type,
            AppointmentEntity appointment) {

        NotificationEntity notification = new NotificationEntity();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setAppointment(appointment);
        notification.setUser(user);
        notification.setSentAt(LocalDateTime.now());
        notification.setIsRead(false);

        NotificationEntity savedNotification = notificationRepository.save(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setNotificationId(savedNotification.getNotificaitonId());
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);
        notificationDTO.setSentAt(savedNotification.getSentAt());

        if (appointment != null) {
            notificationDTO.setAppointmentId(appointment.getAppointmentId());
            notificationDTO.setAppointmentDetails(formatAppointmentDetails(appointment));
        }

        messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/notifications", notificationDTO);
    }

    @Transactional
    @Async
    public void notifyAppointmentCreated(AppointmentEntity appointment) {
        sendNotificationToUser(
                appointment.getCustomer(),
                "Appointment Created",
                "Your appointment has been created and is pending approval",
                NotificationType.APPOINTMENT_CREATED,
                appointment);

        List<UserEntity> admins = userRepository.findByRole(UserRole.ADMIN);
        for(UserEntity admin : admins) {
        sendNotificationToUser(
                admin,
                "New Appointment Assignment",
                "You have been assigned a new appointment pending approval.",
                NotificationType.APPOINTMENT_CREATED,
                appointment);
        }
    }

    @Transactional
    @Async
    public void notifyAppointmentApproved(AppointmentEntity appointment) {
        sendNotificationToUser(appointment.getCustomer(), "Appointment Approved",
                "Your appointment has been approved and confirmed.", NotificationType.APPOINTMENT_APPROVED,
                appointment);

    }

    @Transactional
    @Async
    public void notifyAppointmentRejected(AppointmentEntity appointment, String reason) {
        String message = "Your appointment has been rejected." + (reason != null ? " Reason: " + reason : "");

        sendNotificationToUser(appointment.getCustomer(), "Appointment Rejected",
                message, NotificationType.APPOINTMENT_REJECTED, appointment);
    }

    @Transactional
    @Async
    public void notifyAppointmentCancelled(AppointmentEntity appointment, UserEntity cancelledBy) {
        String message = "Your appointment has been cancelled."
                + (cancelledBy != null ? " Cancelled by: " + cancelledBy.getUsername() : "");

        if (cancelledBy.getUserId() != appointment.getCustomer().getUserId()) {
            sendNotificationToUser(
                    appointment.getCustomer(),
                    "Appointment Cancelled",
                    message,
                    NotificationType.APPOINTMENT_CANCELLED,
                    appointment);
        }

        if (cancelledBy.getRole() == UserRole.CUSTOMER) {
        List<UserEntity> admins = userRepository.findByRole(UserRole.ADMIN);
        for(UserEntity admin : admins) {
            sendNotificationToUser(
                    admin,// we should find a solution for this one . 
                    "Appointment Cancelled",
                    message,
                    NotificationType.APPOINTMENT_CANCELLED,
                    appointment);
        }
        }
    }

    @Transactional
    @Async
    public void markAsRead(Long notificationId, UserEntity user) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    @Async
    public void markAllAsRead(UserEntity user) {
        List<NotificationEntity> unreadNotifications = notificationRepository
                .findByUserAndIsReadFalseOrderBySentAtDesc(user);

        for (NotificationEntity notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public List<NotificationDTO> getUserNotifications(UserEntity user) {
        List<NotificationEntity> notifications = notificationRepository.findByUserOrderBySentAtDesc(user);

        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Long getUnreadCount(UserEntity user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Broadcast notification to all users
     * 
     * @param title   Notification title
     * @param message Notification message
     * @param type    Notification type
     */
    @Transactional
    @Async
    public void broadcastNotification(String title, String message, NotificationType type) {
        userRepository.findAll().forEach(user -> {
            sendNotificationToUser(user, title, message, type, null);
        });
    }

    /**
     * Send notification to a specific user by user ID
     * 
     * @param userId  Target user ID
     * @param title   Notification title
     * @param message Notification message
     * @param type    Notification type
     * @throws RuntimeException if user not found
     */
    @Transactional
    @Async
    public void sendNotificationToUserById(Long userId, String title, String message, NotificationType type) {
        UserEntity targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        sendNotificationToUser(targetUser, title, message, type, null);
    }

    private NotificationDTO convertToDTO(NotificationEntity entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(entity.getNotificaitonId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());

        if (entity.getAppointment() != null) {
            dto.setAppointmentId(entity.getAppointment().getAppointmentId());
            dto.setAppointmentDetails(formatAppointmentDetails(entity.getAppointment()));
        }

        return dto;
    }

    private String formatAppointmentDetails(AppointmentEntity appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return appointment.getStartDateTime().format(formatter) +
                " - " + appointment.getService().getServiceName();
    }
}
