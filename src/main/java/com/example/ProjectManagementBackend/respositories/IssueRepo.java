package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.dto.issue.IssueDetailsLiteDto;
import com.example.ProjectManagementBackend.dto.issue.IssueListDto;
import com.example.ProjectManagementBackend.models.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepo extends JpaRepository<Issue, UUID> {
    @Query("""
select new com.example.ProjectManagementBackend.dto.issue.IssueListDto(
    i.id,
    i.issueKey,
    i.title,
    i.description,
    i.type,
    i.status,
    i.priority,
    i.workspace.id,
    s.id,
    u.id,
    u.firstName,
    u.email,
    i.reporterId,
    i.dueDate,
    i.storyPoints,
    i.createdAt,
    i.updatedAt
)
from Issue i
left join i.sprint s
left join User u on u.id = i.assigneeId
where i.workspace.id = :workspaceId
""")
    List<IssueListDto> findBacklog(UUID workspaceId);
    // All issues in backlog (no sprint assigned)
    List<Issue> findByWorkspaceIdAndSprintIdIsNull(UUID workspaceId);
    // All issues in a specific sprint
    List<Issue> findBySprintId(UUID sprintId);
    // All issues in a workspace


    Page<Issue> findByWorkspaceId(UUID workspaceId, Pageable pageable);
    List<Issue> findByWorkspaceId(UUID workspaceId);

    Issue findByWorkspaceIdAndId(UUID workspaceId,UUID issueId);

    Optional<Issue> findByIdAndWorkspaceId(UUID issueId, UUID workspaceId);

    @Query("""
select new com.example.ProjectManagementBackend.dto.issue.IssueListDto(
    i.id,
    i.issueKey,
    i.title,
    i.description,
    i.type,
    i.status,
    i.priority,
    i.workspace.id,
    s.id,
    u.id,
    u.firstName,
    u.email,
    i.reporterId,
    i.dueDate,
    i.storyPoints,
    i.createdAt,
    i.updatedAt
)
from Issue i
left join i.sprint s
left join User u on u.id = i.assigneeId
where i.workspace.id = :workspaceId
""")
    List<IssueListDto> findAllIssues2(@Param("workspaceId") UUID workspaceId);
}
