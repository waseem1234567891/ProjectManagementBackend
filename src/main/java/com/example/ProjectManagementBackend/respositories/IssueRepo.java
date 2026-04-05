package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IssueRepo extends JpaRepository<Issue, UUID> {
    // All issues in backlog (no sprint assigned)
    List<Issue> findByWorkspaceIdAndSprintIdIsNull(UUID workspaceId);
    // All issues in a specific sprint
    List<Issue> findBySprintId(UUID sprintId);
    // All issues in a workspace
    List<Issue> findByWorkspaceId(UUID workspaceId);

    Issue findByWorkspaceIdAndId(UUID workspaceId,UUID issueId);

    Optional<Issue> findByIdAndWorkspaceId(UUID issueId, UUID workspaceId);
}
