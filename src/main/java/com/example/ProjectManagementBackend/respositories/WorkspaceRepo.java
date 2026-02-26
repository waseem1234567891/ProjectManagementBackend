package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceRepo extends JpaRepository<Workspace, UUID> {
    List<Workspace> findByCreatedBy(UUID userId);

    boolean existsByNameAndCreatedBy(String name, UUID createdBy);
}
