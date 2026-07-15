package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.models.Workspace;
import com.example.ProjectManagementBackend.models.WorkspaceMember;
import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {
    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);


    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(
            UUID workspaceId, UUID userId);

    List<WorkspaceMember> findAllByWorkspaceId(UUID workspaceId);

    boolean existsByWorkspaceAndUserIdAndRoleIn(
            Workspace workspace,
            UUID userId,
            Collection<WorkspaceRole> roles
    );

    boolean existsByWorkspaceAndUser(Workspace workspace, Optional<User> invitedUser);

    Optional<WorkspaceMember> findByWorkspaceIdAndId(UUID workspaceId, UUID memberId);
}
