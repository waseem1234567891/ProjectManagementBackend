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

    private WorkspaceType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkspaceType getType() {
        return type;
    }
}
