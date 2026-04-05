package com.example.ProjectManagementBackend.dto.workspace;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateWorkspaceRequestDto {

    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    // workspace key like PROJ, CRM
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Workspace key must be 2-10 uppercase letters")
    private String key;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}