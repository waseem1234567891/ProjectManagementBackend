package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.models.Notification;
import com.example.ProjectManagementBackend.respositories.NotificationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepo notificationRepo;

    public NotificationService(
            SimpMessagingTemplate messagingTemplate,
            NotificationRepo notificationRepo
    ) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepo = notificationRepo;
    }

    public void sendNotification(UUID userId, String message, UUID issueId) {

        // ✅ Save to DB
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setIssueId(issueId);

        notificationRepo.save(notification);

        // ⚡ Send real-time
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }
}
