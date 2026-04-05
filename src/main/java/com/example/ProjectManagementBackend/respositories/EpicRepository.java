package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EpicRepository extends JpaRepository<Epic, UUID> {
    List<Epic> findByWorkspaceId(UUID workspaceId);
}
