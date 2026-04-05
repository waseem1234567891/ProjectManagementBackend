package com.example.ProjectManagementBackend.dto.issue;

import com.example.ProjectManagementBackend.dto.comments.CommentDTO;
import com.example.ProjectManagementBackend.dto.epic.EpicResponseDto;
import com.example.ProjectManagementBackend.dto.user.UserDto;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class IssueResponseDto {
    private UUID id;
    private String issueKey;
    private String title;
    private String description;
    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;
    private UUID workspaceId;
    private UUID sprintId;
    private UUID assigneeId;
    private List<CommentDTO> comments;
    private UUID reporterId;
    private UserDto Assignee;
    private LocalDate dueDate;
    private Integer storyPoints;
    private Instant createdAt;
    private Instant updatedAt;
    private EpicResponseDto epic;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public UserDto getAssignee() {
        return Assignee;
    }

    public void setAssignee(UserDto assignee) {
        Assignee = assignee;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public EpicResponseDto getEpic() {
        return epic;
    }

    public void setEpic(EpicResponseDto epic) {
        this.epic = epic;
    }
}
