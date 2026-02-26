package com.example.ProjectManagementBackend.dto.workspace;



import com.example.ProjectManagementBackend.models.enums.WorkspaceType;

import java.time.Instant;
import java.util.UUID;

public class WorkspaceResponseDto {

    private UUID id;
    private String name;
    private String description;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private WorkspaceType type;

    public WorkspaceResponseDto() {}

    public WorkspaceResponseDto(
            UUID id,
            String name,
            String description,
            UUID createdBy,
            Instant createdAt,
            Instant updatedAt,
            WorkspaceType type
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type=type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public WorkspaceType getType() {
        return type;
    }

    public void setType(WorkspaceType type) {
        this.type = type;
    }
}

