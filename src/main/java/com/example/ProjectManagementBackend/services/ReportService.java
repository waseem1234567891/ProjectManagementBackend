package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.reports.*;
import com.example.ProjectManagementBackend.models.Issue;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import com.example.ProjectManagementBackend.respositories.IssueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private IssueRepo issueRepo;
    public WorkspaceReportDto generateReport(UUID workspaceId) {

        List<Issue> issues = issueRepo.findByWorkspaceId(workspaceId);

        long totalIssues = issues.size();

        long completed =
                issues.stream()
                        .filter(i -> i.getStatus()
                                == IssueStatus.DONE)
                        .count();
        long inProgress =
                issues.stream()
                        .filter(i -> i.getStatus()
                                == IssueStatus.IN_PROGRESS)
                        .count();

        long todo =
                issues.stream()
                        .filter(i -> i.getStatus()
                                == IssueStatus.TODO)
                        .count();

        double completionRate =
                totalIssues > 0
                        ? ((double) completed / totalIssues) * 100
                        : 0;

        SummaryDto summary =
                new SummaryDto(
                        totalIssues,
                        completed,
                        inProgress,
                        todo,
                        completionRate
                );

        List<MemberPerformanceDto> memberPerformance=issues.stream().filter(issue -> issue.getAssignee()!=null).
        collect(Collectors.groupingBy(Issue::getAssignee,
                Collectors.counting())).entrySet().stream().map(entry->{
                    User member=entry.getKey();
                    return new MemberPerformanceDto(member.getId(),member.getFirstName(),entry.getValue());
                }).collect(Collectors.toList());

        List<StatusDistributionDto> statusDistribution= Arrays.stream(IssueStatus.values()).map(status->{
            long count=issues.stream().filter(
                    issue -> status.equals(issue.getStatus())).count();
            return new StatusDistributionDto(
                    count,
                    status
            );
        }).toList();

        List<IssueTypeDistributionDto> issueTypeDistribution =
                Arrays.stream(IssueType.values())
                        .map(type -> {

                            long count = issues.stream()
                                    .filter(issue ->
                                           type.equals(issue.getType()
                                    ))
                                    .count();

                            return new IssueTypeDistributionDto(
                                    type,
                                    count
                            );
                        })
                        .toList();


        return new WorkspaceReportDto(
                summary,
                statusDistribution,
                memberPerformance,
                issueTypeDistribution

        );


    }
}
