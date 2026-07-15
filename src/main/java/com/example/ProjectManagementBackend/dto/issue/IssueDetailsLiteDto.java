package com.example.ProjectManagementBackend.dto.issue;

import com.example.ProjectManagementBackend.dto.user.UserDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class IssueDetailsLiteDto {

    private UUID id;
    private String issueKey;
    private String title;
    private String description;
    private String type;
    private String status;
    private String priority;

    private UUID workspaceId;
    private UUID sprintId;

    private UserDto assignee;   // ✅ nested DTO
    private UUID reporterId;

    private LocalDate dueDate;
    private Integer storyPoints;

    private Instant createdAt;
    private Instant updatedAt;

    // ✅ Constructor used by JPQL
    public IssueDetailsLiteDto(
            UUID id,
            String issueKey,
            String title,
            String description,
            String type,
            String status,
            String priority,
            UUID workspaceId,
            UUID sprintId,
            UserDto assignee,
            UUID reporterId,
            LocalDate dueDate,
            Integer storyPoints,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.issueKey = issueKey;
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.workspaceId = workspaceId;
        this.sprintId = sprintId;
        this.assignee = assignee;
        this.reporterId = reporterId;
        this.dueDate = dueDate;
        this.storyPoints = storyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public UserDto getAssignee() {
        return assignee;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
