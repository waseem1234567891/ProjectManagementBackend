package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.comments.CommentDTO;
import com.example.ProjectManagementBackend.dto.epic.EpicResponseDto;
import com.example.ProjectManagementBackend.dto.issue.*;
import com.example.ProjectManagementBackend.dto.user.UserDto;
import com.example.ProjectManagementBackend.exceptions.ResourceNotFoundException;
import com.example.ProjectManagementBackend.models.*;
import com.example.ProjectManagementBackend.models.enums.IssuePriority;
import com.example.ProjectManagementBackend.models.enums.IssueStatus;
import com.example.ProjectManagementBackend.models.enums.IssueType;
import com.example.ProjectManagementBackend.respositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private NotificationService notificationService;


    private UUID getCurrentUserId() {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetail.getId();
    }

    public IssueResponseDto createIssue(UUID workspaceId, CreateIssueRequestDto dto) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        // Increment issue counter
        int nextNumber = workspace.getIssueCounter() + 1;
        workspace.setIssueCounter(nextNumber);
        workspaceRepo.save(workspace);

        String issueKey = workspace.getWorkspaceKey() + "-" + nextNumber;

        Issue issue = new Issue();
        issue.setIssueKey(issueKey);
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setType(dto.getType());
        issue.setPriority(dto.getPriority());
        issue.setWorkspace(workspace);
        issue.setAssigneeId(dto.getAssigneeId());
        issue.setReporterId(getCurrentUserId());
        issue.setDueDate(dto.getDueDate());
        issue.setStoryPoints(dto.getStoryPoints());
        issue.setStatus(IssueStatus.TODO);

        // Epic
        if (dto.getEpicId() != null) {
            Epic epic = epicRepository.findById(dto.getEpicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

            issue.setEpic(epic);
        }

        // Subtask Validation / Parent Link
        if (dto.getType() == IssueType.SUBTASK) {

            if (dto.getParentIssueId() == null) {
                throw new IllegalArgumentException("Subtask must have parent issue");
            }

            Issue parent = issueRepo.findById(dto.getParentIssueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent issue not found"));

            if (parent.getType() == IssueType.SUBTASK) {
                throw new IllegalArgumentException("Subtasks cannot have subtasks");
            }

            issue.setParentIssue(parent);

            // Inherit from parent
            issue.setEpic(parent.getEpic());
            issue.setSprint(parent.getSprint());
        }

        if (dto.getType() != IssueType.SUBTASK && dto.getParentIssueId() != null) {
            throw new IllegalArgumentException("Only subtasks can have parent issues");
        }

        Issue savedIssue = issueRepo.save(issue);

        activityService.logActivity(
                "ISSUE_CREATED",
                "Issue " + savedIssue.getTitle() + " created",
                savedIssue.getWorkspace().getId(),
                savedIssue.getId()
        );

        return toDto(savedIssue);
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

    public IssueResponseDto updateIssueStatus(UUID issueId, IssueStatus newStatus) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        // 🔒 CHECK: Prevent parent from moving to DONE if subtasks are not DONE
        if (newStatus == IssueStatus.DONE && issue.getSubtasks() != null && !issue.getSubtasks().isEmpty()) {

            boolean allSubtasksDone = issue.getSubtasks().stream()
                    .allMatch(subtask -> subtask.getStatus() == IssueStatus.DONE);

            if (!allSubtasksDone) {
                throw new IllegalStateException("All subtasks must be DONE before completing this issue");
            }
        }

        // ✅ Update status
        issue.setStatus(newStatus);

        // ✅ Set completed time
        if (newStatus == IssueStatus.DONE) {
            issue.setCompletedAt(Instant.now());
        }

        // ✅ LOG ACTIVITY
        activityService.logActivity(
                "ISSUE_MOVED",
                "Issue " + issue.getTitle() + " moved to " + newStatus,
                issue.getWorkspace().getId(),
                issue.getId()
        );
        if(issue.getAssignee()!=null) {
            notificationService.sendNotification(
                    issue.getAssigneeId(),
                    "Issue " + issue.getTitle() + " moved to " + newStatus,
                    issue.getId()
            );
        }

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


//assigne an issue to a user
    public Issue assignIssue(UUID workspaceId, UUID issueId, UUID assigneeId) {
        Issue issue = issueRepo.findByWorkspaceIdAndId(workspaceId, issueId);
        issue.setAssigneeId(assigneeId);
        notificationService.sendNotification(
                issue.getAssigneeId(),
                "Issue " + issue.getTitle() + " Assign to You By ",
                issue.getId()
        );
        return issueRepo.save(issue);
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
  //assigne an issue to an epic
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
        //return issueRepo.findAllIssues(workspaceId);
           return      issueRepo.findByWorkspaceId(workspaceId).stream().filter(i->i.getType()!=IssueType.SUBTASK).map(this::toDtoCustom).collect(Collectors.toList());
    }

    public IssueResponseDto updateIssueType(UUID issueId, IssueType type) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setType(type);
        return toDto(issueRepo.save(issue));
    }

    public IssueResponseDto updateDueDate(UUID issueId, DateDto dueDate) {
        System.out.println("api hit");
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setDueDate(dueDate.getDueDate());
        return toDto(issueRepo.save(issue));
    }

    public IssueResponseDto updateStoryPoints(UUID issueId, Integer storyPoints) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setStoryPoints(storyPoints);
        return toDto(issueRepo.save(issue));
    }
    private IssueSummaryDto toSummaryDto(Issue issue) {
        if (issue == null) return null;

        IssueSummaryDto dto = new IssueSummaryDto();
        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setTitle(issue.getTitle());
        dto.setStatus(issue.getStatus());
        dto.setType(issue.getType());

        return dto;
    }



    public ResponseEntity<?> createSubtask(UUID workspaceId, UUID parentId, SubtaskRequestDto dto) {

        // 1. Find parent issue
        Issue parent = issueRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent issue not found"));
        Workspace workspace=parent.getWorkspace();
        if (!workspace.getId().equals(workspaceId)) {
            return ResponseEntity.badRequest()
                    .body("Parent issue does not belong to workspace");
        }

        // 🚫 BLOCK SUBTASK → SUBTASK
        if (parent.getType() == IssueType.SUBTASK) {
            return ResponseEntity.badRequest()
                    .body("Subtasks cannot have subtasks");
        }
        // 🚫 Validate title
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Subtask title is required");
        }


        // 2. Create subtask
        // Increment issue counter

        int nextNumber = workspace.getIssueCounter() + 1;
        workspace.setIssueCounter(nextNumber);
        workspaceRepo.save(workspace);

        String issueKey = workspace.getWorkspaceKey() + "-" + nextNumber;



        Issue subtask = new Issue();
        subtask.setTitle(dto.getTitle());
        subtask.setIssueKey(issueKey);
        subtask.setType(IssueType.SUBTASK);
        subtask.setStatus(IssueStatus.TODO);


        subtask.setWorkspace(parent.getWorkspace());
        subtask.setParentIssue(parent);

        subtask.setAssigneeId(parent.getAssigneeId()); // optional default


        subtask.setCreatedAt(Instant.now());
        subtask.setUpdatedAt(Instant.now());
        subtask.setEpic(parent.getEpic());
        subtask.setSprint(parent.getSprint());
        subtask.setReporterId(getCurrentUserId());


        // 3. Save
        Issue saved = issueRepo.save(subtask);

        // 4. Return response
        return ResponseEntity.ok(toDto(saved));



    }


 // Dto to send a complete issues
    private IssueResponseDto toDto(Issue issue) {
        IssueResponseDto dto = new IssueResponseDto();

        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setType(issue.getType());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());
        //dto.setParentId(issue.getParentIssue().getId());

        dto.setWorkspaceId(issue.getWorkspace().getId());

        dto.setSprintId(
                issue.getSprint() != null
                        ? issue.getSprint().getId()
                        : null
        );

        // Assignee Mapping
        UUID assigneeId = issue.getAssigneeId();
        dto.setAssigneeId(assigneeId);

        if (assigneeId != null) {
            User assigneeUser = userRepo.findById(assigneeId).orElse(null);

            if (assigneeUser != null) {
                UserDto assigneeDto = new UserDto();
                assigneeDto.setId(assigneeUser.getId());
                assigneeDto.setName(assigneeUser.getFirstName());
                assigneeDto.setEmail(assigneeUser.getEmail());

                dto.setAssignee(assigneeDto);
            } else {
                dto.setAssignee(null);
            }
        } else {
            dto.setAssignee(null);
        }
        Optional<User> reporterOpt = userRepo.findById(issue.getReporterId());
        User reporter = reporterOpt.get();
        UserDto reporterDto = new UserDto();
        reporterDto.setId(reporter.getId());
        reporterDto.setName(reporter.getFirstName());
        reporterDto.setEmail(reporter.getEmail());

        dto.setReporter(reporterDto);
        dto.setDueDate(issue.getDueDate());
        dto.setStoryPoints(issue.getStoryPoints());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());

        // Comments
        dto.setComments(
                issue.getComments() == null
                        ? List.of()
                        : issue.getComments()
                        .stream()
                        .map(c -> {
                            CommentDTO cdto = new CommentDTO();
                            cdto.setId(c.getId());
                            cdto.setIssueId(issue.getId());
                            cdto.setAuthorId(c.getAuthor().getId());
                            cdto.setAuthorName(
                                    c.getAuthor().getFirstName() + " " +
                                            c.getAuthor().getLastName()
                            );
                            cdto.setContent(c.getContent());
                            cdto.setCreatedAt(c.getCreatedAt());
                            return cdto;
                        })
                        .toList()
        );

        // Epic
        dto.setEpic(
                issue.getEpic() != null
                        ? new EpicResponseDto(issue.getEpic())
                        : null
        );

        // Parent Issue
        dto.setParentIssue(
                issue.getParentIssue() != null
                        ? toSummaryDto(issue.getParentIssue())
                        : null
        );

        // Subtasks
        dto.setSubtasks(
                issue.getSubtasks() == null
                        ? List.of()
                        : issue.getSubtasks()
                        .stream()
                        .map(this::toSummaryDto)
                        .toList()
        );

        return dto;
    }

    private IssueResponseDto toDtoCustom(Issue issue) {
        IssueResponseDto dto = new IssueResponseDto();

        dto.setId(issue.getId());
        dto.setIssueKey(issue.getIssueKey());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setType(issue.getType());
        dto.setStatus(issue.getStatus());
        dto.setPriority(issue.getPriority());

        dto.setWorkspaceId(issue.getWorkspace().getId());

        dto.setSprintId(
                issue.getSprint() != null
                        ? issue.getSprint().getId()
                        : null
        );

        // Assignee Mapping
        UUID assigneeId = issue.getAssigneeId();
        dto.setAssigneeId(assigneeId);

        if (assigneeId != null) {
            User assigneeUser = userRepo.findById(assigneeId).orElse(null);

            if (assigneeUser != null) {
                UserDto assigneeDto = new UserDto();
                assigneeDto.setId(assigneeUser.getId());
                assigneeDto.setName(assigneeUser.getFirstName());
                assigneeDto.setEmail(assigneeUser.getEmail());

                dto.setAssignee(assigneeDto);
            } else {
                dto.setAssignee(null);
            }
        } else {
            dto.setAssignee(null);
        }

     //   dto.setReporterId(issue.getReporterId());
        dto.setDueDate(issue.getDueDate());
        dto.setStoryPoints(issue.getStoryPoints());
        dto.setCreatedAt(issue.getCreatedAt());
        //dto.setUpdatedAt(issue.getUpdatedAt());

        // Comments
//        dto.setComments(
//                issue.getComments() == null
//                        ? List.of()
//                        : issue.getComments()
//                        .stream()
//                        .map(c -> {
//                            CommentDTO cdto = new CommentDTO();
//                            cdto.setId(c.getId());
//                            cdto.setIssueId(issue.getId());
//                            cdto.setAuthorId(c.getAuthor().getId());
//                            cdto.setAuthorName(
//                                    c.getAuthor().getFirstName() + " " +
//                                            c.getAuthor().getLastName()
//                            );
//                            cdto.setContent(c.getContent());
//                            cdto.setCreatedAt(c.getCreatedAt());
//                            return cdto;
//                        })
//                        .toList()
//        );

        // Epic
        dto.setEpic(
                issue.getEpic() != null
                        ? new EpicResponseDto(issue.getEpic())
                        : null
        );

        // Parent Issue
//        dto.setParentIssue(
//                issue.getParentIssue() != null
//                        ? toSummaryDto(issue.getParentIssue())
//                        : null
//        );

        // Subtasks
//        dto.setSubtasks(
//                issue.getSubtasks() == null
//                        ? List.of()
//                        : issue.getSubtasks()
//                        .stream()
//                        .map(this::toSummaryDto)
//                        .toList()
//        );
        Optional<User> reporterOpt = userRepo.findById(issue.getReporterId());
        User reporter = reporterOpt.get();
        UserDto reporterDto = new UserDto();
        reporterDto.setId(reporter.getId());
        reporterDto.setName(reporter.getFirstName());
        reporterDto.setEmail(reporter.getEmail());

        dto.setReporter(reporterDto);
        return dto;
    }


// update Issue Sprint
    public IssueResponseDto updateIssueSprint(UUID workspaceId, UUID issueId, UUID sprintId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        if (sprintId!=null) {
            Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
            issue.setSprint(sprint);
        }else {
            issue.setSprint(null); // null = backlog
        }

        return toDto(issueRepo.save(issue));
    }

    public Page<IssueResponseDto> getAllIssuesofWorkspaceUsingPagination(UUID workspaceId, Pageable pageable) {
        Page<Issue> issuesPage =
                issueRepo.findByWorkspaceId(workspaceId, pageable);
        return issuesPage.map(this::toDto);
    }
}
