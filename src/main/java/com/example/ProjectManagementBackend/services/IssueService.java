package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.comments.CommentDTO;
import com.example.ProjectManagementBackend.dto.epic.EpicResponseDto;
import com.example.ProjectManagementBackend.dto.issue.CreateIssueRequestDto;
import com.example.ProjectManagementBackend.dto.issue.IssueResponseDto;
import com.example.ProjectManagementBackend.dto.user.UserDto;
import com.example.ProjectManagementBackend.exceptions.ResourceNotFoundException;
import com.example.ProjectManagementBackend.models.*;
import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import com.example.ProjectManagementBackend.respositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IssueService {

    @Autowired
    private IssueRepo issueRepo;
    @Autowired
    private WorkspaceRepo workspaceRepo;
    @Autowired
    private SprintRepo sprintRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private EpicRepository epicRepository;


    private UUID getCurrentUserId() {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetail.getId();
    }

    public IssueResponseDto createIssue(UUID workspaceId, CreateIssueRequestDto dto) {
        Workspace workspace = workspaceRepo.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        // increment issue counter
        int nextNumber = workspace.getIssueCounter() + 1;
        workspace.setIssueCounter(nextNumber);
        Workspace saveWorkspace = workspaceRepo.save(workspace);

        // generate key like DEV-1
        String issueKey = workspace.getWorkspaceKey() + "-" + nextNumber;

        Issue issue = new Issue();
        issue.setIssueKey(issueKey);
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setType(dto.getType());
        issue.setPriority(dto.getPriority());
        issue.setWorkspace(saveWorkspace);
        issue.setAssigneeId(dto.getAssigneeId());
        issue.setReporterId(getCurrentUserId());
        issue.setDueDate(dto.getDueDate());
        issue.setStoryPoints(dto.getStoryPoints());
        issue.setStatus(IssueStatus.TODO);
        Optional<Epic> byId = epicRepository.findById(dto.getEpicId());
        if (byId.isPresent()) {
            issue.setEpic(byId.get());
        }

        // ✅ LOG ACTIVITY
        activityService.logActivity(
                "ISSUE_CREATED",
                "Issue " + issue.getTitle() + " created",
                issue.getWorkspace().getId(),
                issue.getId()
        );

        return toDto(issueRepo.save(issue));
    }

    // Get all backlog issues (not in any sprint)
    public List<IssueResponseDto> getBacklogIssues(UUID workspaceId) {
        return issueRepo.findByWorkspaceIdAndSprintIdIsNull(workspaceId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Get all issues in a specific sprint
    public List<IssueResponseDto> getIssuesBySprint(UUID sprintId) {
        return issueRepo.findBySprintId(sprintId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Move an issue to a sprint (or back to backlog if sprintId is null)
    public IssueResponseDto moveIssueToSprint(UUID issueId, UUID sprintId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        Optional<Sprint> optionalSprint = sprintRepo.findById(sprintId);
        if (optionalSprint.isPresent()) {
            Sprint sprint=optionalSprint.get();
            issue.setSprint(sprint);
        }else {
            throw new ResourceNotFoundException("sprint not found");
        }
        return toDto(issueRepo.save(issue));
    }

    // Update issue status (e.g. drag & drop on Kanban)
    public IssueResponseDto updateIssueStatus(UUID issueId, IssueStatus newStatus) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        issue.setStatus(newStatus);
        // ✅ LOG ACTIVITY HERE
        activityService.logActivity(
                "ISSUE_MOVED",
                "Issue " + issue.getTitle() + " moved to " + newStatus,
                issue.getWorkspace().getId(),
                issue.getId()
        );
        return toDto(issueRepo.save(issue));
    }

    // Update full issue details
    public IssueResponseDto updateIssue(UUID issueId, CreateIssueRequestDto dto) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setType(dto.getType());
        issue.setPriority(dto.getPriority());
        issue.setAssigneeId(dto.getAssigneeId());
        issue.setDueDate(dto.getDueDate());
        issue.setStoryPoints(dto.getStoryPoints());
        return toDto(issueRepo.save(issue));
    }

    public void deleteIssue(UUID issueId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        issueRepo.delete(issue);
    }

    public IssueResponseDto getIssueById(UUID workspaceId, UUID issueId) {
        Optional<Issue> byIdAndWorkspaceId = issueRepo.findByIdAndWorkspaceId(issueId, workspaceId);
        if (byIdAndWorkspaceId.isPresent()) {
            Issue issue=byIdAndWorkspaceId.get();
            return toDto(issue);
        }
        else throw new ResourceNotFoundException("issue not found");
    }

    private IssueResponseDto toDto(Issue issue) {
        IssueResponseDto dto = new IssueResponseDto();
        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setType(issue.getType());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());
        dto.setWorkspaceId(issue.getWorkspace().getId());

        // ✅ Null-safe for sprint
        if (issue.getSprint() != null) {
            dto.setSprintId(issue.getSprint().getId());
        } else {
            dto.setSprintId(null); // or leave it unset
        }

        // Assume issue.getAssigneeId() may be null
        UUID assigneeId = issue.getAssigneeId();
        User assigneeUser = null;
        if (assigneeId != null) {
            // Safe to query repository
            assigneeUser = userRepo.findById(assigneeId)
                    .orElse(null); // returns null if user not found
        }
        // Set assignee in DTO
        if (assigneeUser != null) {
            UserDto assigneeDto = new UserDto();
            assigneeDto.setId(assigneeUser.getId());
            assigneeDto.setName(assigneeUser.getFirstName());
            assigneeDto.setEmail(assigneeUser.getEmail());
            dto.setAssignee(assigneeDto);
        } else {
            dto.setAssignee(null); // explicitly null if no assignee
        }

        dto.setAssigneeId(assigneeId); // still include ID, may be null
        dto.setReporterId(issue.getReporterId());
        dto.setDueDate(issue.getDueDate());
        if(issue.getComments()!=null)
        {
            List<CommentDTO> commentDTOS=issue.getComments().stream().map(c->{
                CommentDTO cdto=new CommentDTO();
                cdto.setId(c.getId());
                cdto.setIssueId(issue.getId());
                cdto.setAuthorId(c.getAuthor().getId());
                cdto.setAuthorName(c.getAuthor().getFirstName() + " " + c.getAuthor().getLastName());
                cdto.setContent(c.getContent());
                cdto.setCreatedAt(c.getCreatedAt());
             return cdto;
            }).toList();
            dto.setComments(commentDTOS);
        }

        dto.setStoryPoints(issue.getStoryPoints());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());
        if (issue.getEpic()!=null) {
            dto.setEpic(new EpicResponseDto(issue.getEpic()));
        }
        return dto;
    }

    public Issue assignIssue(UUID workspaceId, UUID issueId, UUID assigneeId) {
        Issue byWorkspaceIdAndId = issueRepo.findByWorkspaceIdAndId(workspaceId, issueId);
    byWorkspaceIdAndId.setAssigneeId(assigneeId);
    return issueRepo.save(byWorkspaceIdAndId);
    }

    public IssueResponseDto unassignIssue(UUID workspaceId, UUID issueId) {
        Issue issue = issueRepo.findByIdAndWorkspaceId(issueId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        issue.setAssigneeId(null);

        Issue saved = issueRepo.save(issue);
        return toDto(saved);
    }

    public IssueResponseDto updateIssuePriority(UUID workspaceId,UUID issueId, IssuePriority priority) {
        Issue issue = issueRepo.findByIdAndWorkspaceId(issueId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        issue.setPriority(priority);

        Issue saved = issueRepo.save(issue);
        return toDto(saved);
    }

    public Issue assignEpic(UUID issueId, UUID epicId) {

        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        issue.setEpic(epic);

        issueRepo.save(issue);

        return issue;
    }

    public IssueResponseDto updateIssueEpic(UUID issueId, UUID epicId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new RuntimeException("Epic not found"));

        issue.setEpic(epic);

        issueRepo.save(issue);

        return toDto(issue);
    }
    public IssueResponseDto removeEpic(UUID issueId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setEpic(null);

        issueRepo.save(issue);

        return toDto(issue);
    }

    public List<IssueResponseDto> getAllIssuesofWorkspace(UUID workspaceId) {
        return issueRepo.findByWorkspaceId(workspaceId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public IssueResponseDto updateIssueType(UUID issueId, IssueType type) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setType(type);
        return toDto(issueRepo.save(issue));
    }

    public IssueResponseDto updateDueDate(UUID issueId, LocalDate dueDate) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setDueDate(dueDate);
        return toDto(issueRepo.save(issue));
    }

    public IssueResponseDto updateStoryPoints(UUID issueId, Integer storyPoints) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setStoryPoints(storyPoints);
        return toDto(issueRepo.save(issue));
    }
}
