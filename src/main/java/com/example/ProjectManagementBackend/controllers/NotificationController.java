package com.example.ProjectManagementBackend.controllers;





import com.example.ProjectManagementBackend.models.Notification;
import com.example.ProjectManagementBackend.respositories.NotificationRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepo repo;

    public NotificationController(NotificationRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Notification> getNotifications(@RequestParam UUID userId) {
        return repo.findTop15ByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
        repo.save(n);
    }
}
