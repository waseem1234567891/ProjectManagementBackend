package com.example.ProjectManagementBackend.dto.reports;

import com.example.ProjectManagementBackend.models.enums.IssueStatus;

public class StatusDistributionDto {
    private IssueStatus status;
    private Long count;

    public StatusDistributionDto() {
    }

    public StatusDistributionDto(Long count, IssueStatus status) {
        this.count = count;
        this.status = status;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
