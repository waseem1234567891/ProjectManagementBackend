package com.example.ProjectManagementBackend.dto.sprint;

import com.example.ProjectManagementBackend.models.Sprint;
import com.example.ProjectManagementBackend.models.enums.SprintStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class SprintResponseDto {
    private UUID id;
    private String name;
    private String goal;
    private UUID workspaceId;
    private SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant createdAt;

    public SprintResponseDto() {
    }

    public SprintResponseDto(Sprint updatedSprint) {
        this.id=updatedSprint.getId();
        this.name=updatedSprint.getName();
        this.goal= updatedSprint.getGoal();
        this.startDate=updatedSprint.getStartDate();
        this.endDate=updatedSprint.getEndDate();
        this.workspaceId=updatedSprint.getWorkspace().getId();
        this.createdAt=updatedSprint.getCreatedAt();
        this.status=updatedSprint.getStatus();

    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public UUID getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(UUID workspaceId) { this.workspaceId = workspaceId; }

    public SprintStatus getStatus() { return status; }
    public void setStatus(SprintStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
