package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Sprint;
import com.example.ProjectManagementBackend.models.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SprintRepo extends JpaRepository<Sprint, UUID> {
    List<Sprint> findByWorkspaceId(UUID workspaceId);
    Optional<Sprint> findByWorkspaceIdAndStatus(UUID workspaceId, SprintStatus status);

   Optional <Sprint> findByWorkspaceIdAndId(UUID workspaceId, UUID sprintId);
}
