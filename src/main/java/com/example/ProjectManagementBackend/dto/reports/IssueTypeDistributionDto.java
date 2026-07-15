package com.example.ProjectManagementBackend.dto.reports;

import com.example.ProjectManagementBackend.models.enums.IssueType;

public class IssueTypeDistributionDto {
    private IssueType type;
    private Long count;

    public IssueTypeDistributionDto() {
    }

    public IssueTypeDistributionDto(IssueType type, Long count) {
        this.type = type;
        this.count = count;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
