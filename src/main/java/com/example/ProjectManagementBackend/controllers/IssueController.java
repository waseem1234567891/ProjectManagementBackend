package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.issue.CreateIssueRequestDto;
import com.example.ProjectManagementBackend.dto.issue.IssueResponseDto;
import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import com.example.ProjectManagementBackend.services.IssueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspace/{workspaceId}/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    // POST /workspace/{workspaceId}/issues — create an issue
    @PostMapping
    public ResponseEntity<IssueResponseDto> createIssue(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateIssueRequestDto dto) {
        return ResponseEntity.ok(issueService.createIssue(workspaceId, dto));
    }

    // GET /workspace/{workspaceId}/issues/backlog — get backlog issues
    @GetMapping("/backlog")
    public ResponseEntity<List<IssueResponseDto>> getBacklog(@PathVariable UUID workspaceId) {
        return ResponseEntity.ok(issueService.getBacklogIssues(workspaceId));
    }
    // GET /workspace/{workspaceId}/issues - get all issues of workspace
    @GetMapping("")
    public ResponseEntity<List<IssueResponseDto>> getAllIssuesOfWorkspace(@PathVariable UUID workspaceId) {
        return ResponseEntity.ok(issueService.getAllIssuesofWorkspace(workspaceId));
    }

    // GET /workspace/{workspaceId}/issues/sprint/{sprintId} — get issues for a sprint
    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<IssueResponseDto>> getIssuesBySprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId) {
        return ResponseEntity.ok(issueService.getIssuesBySprint(sprintId));
    }

    // GET /workspace/{workspaceId}/issues/{issueId} — get single issue
    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> getIssue(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId) {
        return ResponseEntity.ok(issueService.getIssueById(workspaceId,issueId));
    }

    // PATCH /workspace/{workspaceId}/issues/{issueId}/move?sprintId=... — move to sprint or backlog
    @PatchMapping("/{issueId}/move")
    public ResponseEntity<IssueResponseDto> moveIssue(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam(required = false) UUID sprintId) {
        return ResponseEntity.ok(issueService.moveIssueToSprint(issueId, sprintId));
    }

    // PATCH /workspace/{workspaceId}/issues/{issueId}/status?status=IN_PROGRESS — update status
    @PatchMapping("/{issueId}/status")
    public ResponseEntity<IssueResponseDto> updateStatus(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam IssueStatus status) {
        return ResponseEntity.ok(issueService.updateIssueStatus(issueId, status));
    }
    // PATCH /workspace/{workspaceId}/issues/{issueId}/type?type=STORY — update type
    @PatchMapping("/{issueId}/type")
    public ResponseEntity<IssueResponseDto> updateType(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam IssueType type) {
        return ResponseEntity.ok(issueService.updateIssueType(issueId, type));
    }
    // PATCH /workspace/{workspaceId}/issues/{issueId}/dueDate
    @PatchMapping("/{issueId}/due-date")
    public ResponseEntity<IssueResponseDto> updateDueDate(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam LocalDate dueDate) {
        return ResponseEntity.ok(issueService.updateDueDate(issueId, dueDate));
    }

    // PATCH /workspace/{workspaceId}/issues/{issueId}/storyPoints
    @PatchMapping("/{issueId}/storyPoints")
    public ResponseEntity<IssueResponseDto> updateStoryPoints(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam Integer storyPoints) {
        return ResponseEntity.ok(issueService.updateStoryPoints(issueId, storyPoints));
    }


    @PatchMapping("/{issueId}/epic")
    public ResponseEntity<IssueResponseDto> updateEpic(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam UUID epicId) {
        return ResponseEntity.ok(issueService.updateIssueEpic(issueId, epicId));
    }

    @PatchMapping("/{issueId}/epic/remove")
    public ResponseEntity<IssueResponseDto> removeEpic(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId) {

        return ResponseEntity.ok(issueService.removeEpic(issueId));
    }



    // PATCH /workspace/{workspaceId}/issues/{issueId}/status?status=IN_PROGRESS — update status
    @PatchMapping("/{issueId}/priority")
    public ResponseEntity<IssueResponseDto> updatePriority(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @RequestParam IssuePriority priority) {
        return ResponseEntity.ok(issueService.updateIssuePriority(workspaceId,issueId, priority));
    }

    // PUT /workspace/{workspaceId}/issues/{issueId} — full update
    @PatchMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> updateIssue(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId,
            @Valid @RequestBody CreateIssueRequestDto dto) {
        return ResponseEntity.ok(issueService.updateIssue(issueId, dto));
    }

    // DELETE /workspace/{workspaceId}/issues/{issueId}
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId) {
        issueService.deleteIssue(issueId);
        return ResponseEntity.noContent().build();
    }
    // PUT/workspace/{workspaceId}/issues/{issueId}/assign/{assigneeId}
    @PutMapping("/{issueId}/assign/{assigneeId}")
    public ResponseEntity<?> assignAnIssueToAUser(@PathVariable UUID workspaceId, @PathVariable UUID issueId,@PathVariable UUID assigneeId)
    {
     return ResponseEntity.ok( issueService.assignIssue(workspaceId,issueId,assigneeId));
    }
    @PutMapping("/{issueId}/unassign")
    public ResponseEntity<IssueResponseDto> unassignIssue(
            @PathVariable UUID workspaceId,
            @PathVariable UUID issueId) {

        IssueResponseDto response = issueService.unassignIssue(workspaceId, issueId);
        return ResponseEntity.ok(response);
    }
}
