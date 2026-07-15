package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.sprint.CreateSprintRequestDto;
import com.example.ProjectManagementBackend.dto.sprint.EditSprintDto;
import com.example.ProjectManagementBackend.dto.sprint.SprintResponseDto;
import com.example.ProjectManagementBackend.exceptions.ResourceNotFoundException;
import com.example.ProjectManagementBackend.models.Issue;
import com.example.ProjectManagementBackend.models.Sprint;
import com.example.ProjectManagementBackend.models.Workspace;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.SprintStatus;
import com.example.ProjectManagementBackend.respositories.IssueRepo;
import com.example.ProjectManagementBackend.respositories.SprintRepo;
import com.example.ProjectManagementBackend.respositories.WorkspaceRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SprintService {

    @Autowired
    private SprintRepo sprintRepo;

    @Autowired
    private WorkspaceRepo workspaceRepo;

    @Autowired
    private IssueRepo issueRepo;

    @Autowired
    private ActivityService activityService;



    public SprintResponseDto createSprint(UUID workspaceId, CreateSprintRequestDto dto) {
        Sprint sprint = new Sprint();
        sprint.setName(dto.getName());
        sprint.setGoal(dto.getGoal());
        Optional<Workspace> optnionalWorkspace = workspaceRepo.findById(workspaceId);
        if (optnionalWorkspace.isPresent())
        {
            Workspace workspace=optnionalWorkspace.get();
            sprint.setWorkspace(workspace);
            sprint.setStartDate(dto.getStartDate());
            sprint.setEndDate(dto.getEndDate());
            sprint.setStatus(SprintStatus.PLANNING);
        } else {
            throw new RuntimeException("Workspace not found");
        }

        return toDto(sprintRepo.save(sprint));
    }

    public List<SprintResponseDto> getSprintsByWorkspace(UUID workspaceId) {
        return sprintRepo.findByWorkspaceId(workspaceId)
                .stream().filter(s->s.getStatus()==SprintStatus.PLANNING||s.getStatus()==SprintStatus.ACTIVE).map(this::toDto).collect(Collectors.toList());
    }

    public SprintResponseDto startSprint(UUID sprintId) {
        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        // Only one active sprint per workspace at a time
        sprintRepo.findByWorkspaceIdAndStatus(sprint.getWorkspace().getId(), SprintStatus.ACTIVE)
                .ifPresent(active -> {
                    throw new IllegalStateException("There is already an active sprint in this workspace");
                });
        sprint.setStatus(SprintStatus.ACTIVE);
        return toDto(sprintRepo.save(sprint));
    }

    @Transactional
    public SprintResponseDto completeSprint(UUID sprintId) {

        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        if (sprint.getStatus() != SprintStatus.ACTIVE) {
            throw new RuntimeException("Invalid Sprint");
        }
         sprint.setCompletedAt(LocalDateTime.now());
        List<Issue> issues = sprint.getIssues();

        List<Issue> unfinishedIssues = issues.stream()
                .filter(i -> i.getStatus() != IssueStatus.DONE)
                .toList();

        List<Issue> finishedIssues = issues.stream()
                .filter(i -> i.getStatus() == IssueStatus.DONE)
                .toList();

        // ✅ 1. Create next sprint (FULLY initialized)
        Sprint nextSprint = new Sprint();
        nextSprint.setName(generateNextSprintName(sprint.getWorkspace().getId()));
        nextSprint.setStatus(SprintStatus.ACTIVE);
        nextSprint.setWorkspace(sprint.getWorkspace());
        nextSprint.setStartDate(LocalDate.now());
        nextSprint.setEndDate(LocalDate.now().plusDays(14));

        sprintRepo.save(nextSprint);

        // ✅ 2. Move unfinished issues
        unfinishedIssues.forEach(issue -> issue.setSprint(nextSprint));
        issueRepo.saveAll(unfinishedIssues);

        // ✅ 3. Save metrics
        int completedCount = finishedIssues.size();

        int velocity = finishedIssues.stream()
                .mapToInt(i -> i.getStoryPoints() != null ? i.getStoryPoints() : 0)
                .sum();


        sprint.setVelocity(velocity);

        // ✅ 4. Complete sprint
        sprint.setStatus(SprintStatus.COMPLETED);
        sprint.setEndDate(LocalDate.now());

        sprintRepo.save(sprint);

        activityService.logActivity(
                "SPRINT_COMPLETED",
                sprint.getName() + " completed",
                sprint.getWorkspace().getId(),
                null
        );

        // ✅ 5. Return completed sprint (better UX)
        return toDto(sprint);
    }

    private String generateNextSprintName(UUID workspaceId) {

        List<Sprint> sprints = sprintRepo.findByWorkspaceId(workspaceId);

        int max = sprints.stream()
                .map(Sprint::getName)
                .filter(name -> name.startsWith("Sprint "))
                .map(name -> {
                    try {
                        return Integer.parseInt(name.replace("Sprint ", ""));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return "Sprint " + (max + 1);
    }

    public SprintResponseDto getActiveSprint(UUID workspaceId) {
        return sprintRepo.findByWorkspaceIdAndStatus(workspaceId, SprintStatus.ACTIVE)
                .map(this::toDto)
                .orElseGet(() -> {
                    SprintResponseDto dto = new SprintResponseDto();
                    dto.setMessage("No active sprint found");
                    return dto;
                });
    }

    public void deleteSprint(UUID sprintId) {
        Sprint sprint = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        List<Issue> issues = issueRepo.findBySprintId(sprintId);
        issues.stream().forEach(issue->issue.setSprint(null));
        issueRepo.saveAll(issues);
        sprintRepo.delete(sprint);
    }

    private SprintResponseDto toDto(Sprint sprint) {
        SprintResponseDto dto = new SprintResponseDto();
        dto.setId(sprint.getId());
        dto.setName(sprint.getName());
        dto.setGoal(sprint.getGoal());
        dto.setWorkspaceId(sprint.getWorkspace().getId());
        dto.setStatus(sprint.getStatus());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setCreatedAt(sprint.getCreatedAt());
        return dto;
    }

    public Sprint updateSprint(UUID workspaceId, UUID sprintId, EditSprintDto dto) {

        Sprint sprint = sprintRepo
                .findByWorkspaceIdAndId(workspaceId, sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        Optional.ofNullable(dto.getName()).ifPresent(sprint::setName);
        Optional.ofNullable(dto.getGoal()).ifPresent(sprint::setGoal);
        Optional.ofNullable(dto.getStartDate()).ifPresent(sprint::setStartDate);
        Optional.ofNullable(dto.getEndDate()).ifPresent(sprint::setEndDate);

      return   sprintRepo.save(sprint);
    }

    public Sprint getSprintById(UUID workspaceId, UUID sprintId) {
        Sprint sprint = sprintRepo
                .findByWorkspaceIdAndId(workspaceId, sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        return sprint;
    }
}
