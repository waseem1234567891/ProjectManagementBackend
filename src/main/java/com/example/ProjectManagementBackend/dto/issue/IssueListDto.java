package com.example.ProjectManagementBackend.dto.issue;

import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class IssueListDto {

    private UUID id;
    private String issueKey;
    private String title;
    private String description;

    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;

    private UUID workspaceId;
    private UUID sprintId;

    // flat user fields (from join)
    private UUID assigneeId;
    private String assigneeName;
    private String assigneeEmail;

    private UUID reporterId;
    private LocalDate dueDate;
    private Integer storyPoints;

    private Instant createdAt;
    private Instant updatedAt;

    public IssueListDto() {}

    public IssueListDto(
            UUID id,
            String issueKey,
            String title,
            String description,
            IssueType type,
            IssueStatus status,
            IssuePriority priority,
            UUID workspaceId,
            UUID sprintId,
            UUID assigneeId,
            String assigneeName,
            String assigneeEmail,
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
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.assigneeEmail = assigneeEmail;
        this.reporterId = reporterId;
        this.dueDate = dueDate;
        this.storyPoints = storyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters + setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getIssueKey() { return issueKey; }
    public void setIssueKey(String issueKey) { this.issueKey = issueKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public IssueType getType() { return type; }
    public void setType(IssueType type) { this.type = type; }

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }

    public IssuePriority getPriority() { return priority; }
    public void setPriority(IssuePriority priority) { this.priority = priority; }

    public UUID getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(UUID workspaceId) { this.workspaceId = workspaceId; }

    public UUID getSprintId() { return sprintId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }

    public UUID getAssigneeId() { return assigneeId; }
    public void setAssigneeId(UUID assigneeId) { this.assigneeId = assigneeId; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public String getAssigneeEmail() { return assigneeEmail; }
    public void setAssigneeEmail(String assigneeEmail) { this.assigneeEmail = assigneeEmail; }

    public UUID getReporterId() { return reporterId; }
    public void setReporterId(UUID reporterId) { this.reporterId = reporterId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

}