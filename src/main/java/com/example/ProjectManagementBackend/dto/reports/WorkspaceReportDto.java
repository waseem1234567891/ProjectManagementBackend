package com.example.ProjectManagementBackend.dto.reports;

import java.util.List;

public class WorkspaceReportDto {
    private SummaryDto summary;

    private List<StatusDistributionDto> statusDistribution;

    private List<MemberPerformanceDto> memberPerformance;

    private List<IssueTypeDistributionDto> issueTypeDistribution;

    public WorkspaceReportDto() {
    }

    public WorkspaceReportDto(SummaryDto summary, List<StatusDistributionDto> statusDistribution, List<MemberPerformanceDto> memberPerformance, List<IssueTypeDistributionDto> issueTypeDistribution) {
        this.summary = summary;
        this.statusDistribution = statusDistribution;
        this.memberPerformance = memberPerformance;
        this.issueTypeDistribution=issueTypeDistribution;
    }

    public SummaryDto getSummary() {
        return summary;
    }

    public void setSummary(SummaryDto summary) {
        this.summary = summary;
    }

    public List<StatusDistributionDto> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(List<StatusDistributionDto> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public List<MemberPerformanceDto> getMemberPerformance() {
        return memberPerformance;
    }

    public void setMemberPerformance(List<MemberPerformanceDto> memberPerformance) {
        this.memberPerformance = memberPerformance;
    }

    public List<IssueTypeDistributionDto> getIssueTypeDistribution() {
        return issueTypeDistribution;
    }

    public void setIssueTypeDistribution(List<IssueTypeDistributionDto> issueTypeDistribution) {
        this.issueTypeDistribution = issueTypeDistribution;
    }
}
