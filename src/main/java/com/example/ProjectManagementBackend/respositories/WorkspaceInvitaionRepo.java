package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Workspace;
import com.example.ProjectManagementBackend.models.WorkspaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitaionRepo extends JpaRepository<WorkspaceInvitation, UUID> {
    Optional<Object> findByToken(String token);

     Optional<WorkspaceInvitation> findByWorkspaceAndEmail(Workspace workspace, String email);
}
