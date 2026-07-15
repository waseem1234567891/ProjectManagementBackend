package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Notification> findTop15ByUserIdOrderByCreatedAtDesc(UUID userId);
}
