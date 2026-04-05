package com.example.ProjectManagementBackend.dto.workspace;

import com.example.ProjectManagementBackend.models.enums.WorkspaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateWorkspaceRequestDto {

    @NotBlank(message = "Workspace name is required")
    @Size(min = 3, max = 100, message = "Workspace name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Workspace key is required")
    @Size(min = 2, max = 10, message = "Workspace key must be 2–10 characters")
    private String workspaceKey;

    private WorkspaceType type;

    // GETTERS

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWorkspaceKey() {
        return workspaceKey;
    }

    public WorkspaceType getType() {
        return type;
    }

    // SETTERS

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWorkspaceKey(String workspaceKey) {
        this.workspaceKey = workspaceKey;
    }

    public void setType(WorkspaceType type) {
        this.type = type;
    }
}