package com.example.ProjectManagementBackend.dto.workspace;

import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;

public class ChangeRoleRequest {

    private WorkspaceRole role;

    public WorkspaceRole getRole() {
        return role;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }
}
