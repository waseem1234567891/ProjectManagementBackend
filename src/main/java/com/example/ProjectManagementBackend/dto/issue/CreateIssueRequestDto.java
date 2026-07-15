package com.example.ProjectManagementBackend.dto.issue;

import com.example.ProjectManagementBackend.models.Issue;
import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateIssueRequestDto {

    @NotBlank(message = "Issue title is required")
    private String title;
    private UUID parentIssueId;

    private String description;
    private IssueType type = IssueType.TASK;
    private IssuePriority priority = IssuePriority.MEDIUM;
    private UUID assigneeId;
    private LocalDate dueDate;
    private Integer storyPoints;
    // if provided, issue is created inside a sprint; otherwise goes to backlog
    private UUID sprintId;
    private UUID epicId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public IssueType getType() { return type; }
    public void setType(IssueType type) { this.type = type; }

    public IssuePriority getPriority() { return priority; }
    public void setPriority(IssuePriority priority) { this.priority = priority; }

    public UUID getAssigneeId() { return assigneeId; }
    public void setAssigneeId(UUID assigneeId) { this.assigneeId = assigneeId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }

    public UUID getSprintId() { return sprintId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }

    public UUID getEpicId() {
        return epicId;
    }

    public void setEpicId(UUID epicId) {
        this.epicId = epicId;
    }

    public UUID getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(UUID parentIssueId) {
        this.parentIssueId = parentIssueId;
    }
}
