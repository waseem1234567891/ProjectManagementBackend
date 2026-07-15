package com.example.ProjectManagementBackend.dto.issue;

import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;

import java.util.UUID;

public class IssueSummaryDto {

    private UUID id;
    private String issueKey;
    private String title;
    private IssueStatus status;
    private IssueType type;

    // getters/setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }
}
