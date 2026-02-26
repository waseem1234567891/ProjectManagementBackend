package com.example.ProjectManagementBackend.dto.workspace;

import com.example.ProjectManagementBackend.models.enums.InvitationStatus;
import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;

import java.time.Instant;
import java.util.UUID;

public class WorkspaceInvitationResponseDto {

    private UUID id;
    private UUID workspaceId;
    private String email;
    private WorkspaceRole role;
    private InvitationStatus status;
    private Instant expiresAt;
}

