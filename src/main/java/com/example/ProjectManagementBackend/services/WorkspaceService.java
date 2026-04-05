package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.workspace.*;
import com.example.ProjectManagementBackend.exceptions.AccessDeniedException;
import com.example.ProjectManagementBackend.exceptions.ResourceNotFoundException;
import com.example.ProjectManagementBackend.exceptions.WorkspaceNameAlreadyExistException;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.models.Workspace;
import com.example.ProjectManagementBackend.models.WorkspaceInvitation;
import com.example.ProjectManagementBackend.models.WorkspaceMember;
import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import com.example.ProjectManagementBackend.respositories.WorkspaceInvitaionRepo;
import com.example.ProjectManagementBackend.respositories.WorkspaceMemberRepository;
import com.example.ProjectManagementBackend.respositories.WorkspaceRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepo workspaceRepo;
    @Autowired UserService userService;

    @Autowired
    private WorkspaceInvitaionRepo workspaceInvitaionRepo;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired EmailService emailService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    WorkspaceMemberRepository memberRepository;

    //create workspace
    public ResponseEntity<?> createWorkspace(CreateWorkspaceRequestDto dto) {

        User user = userService.getCurrentUser();

        if (workspaceRepo.existsByNameAndCreatedBy(dto.getName(), user.getId())) {
            throw new WorkspaceNameAlreadyExistException("Workspace with same name already exists");
        }
        Workspace workspace=new Workspace();
        workspace.setName(dto.getName());
        workspace.setDescription(dto.getDescription());
        workspace.setCreatedBy(user.getId());
        System.out.println(user.getId());
        workspace.setType(dto.getType());
        workspace.setWorkspaceKey(dto.getWorkspaceKey());
        Workspace saved = workspaceRepo.save(workspace);
        //create a workspace member
        WorkspaceMember workspaceMember=new WorkspaceMember();
        workspaceMember.setWorkspace(saved);
        workspaceMember.setUser(user);
        workspaceMember.setRole(WorkspaceRole.OWNER);
        workspaceMemberRepository.save(workspaceMember);

        return ResponseEntity.status(201).body(mapToDto(saved));
    }
    //update workspace
    public WorkspaceResponseDto updateWorkspace(UUID workspaceId, UpdateWorkspaceRequestDto dto) {

        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        UUID currentUserId = userService.getCurrentUser().getId();

        if (!workspace.getCreatedBy().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to update this workspace");
        }

        if (dto.getName() != null) {
            workspace.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            workspace.setDescription(dto.getDescription());
        }

        Workspace updated = workspaceRepo.save(workspace);
        return mapToDto(updated);
    }

    // ---------------- GET BY ID ----------------

    public WorkspaceResponseDto getWorkspaceById(UUID workspaceId) {

        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return mapToDto(workspace);
    }

    // ---------------- GET MY WORKSPACES ----------------
    public List<WorkspaceResponseDto> getMyWorkspaces() {

        UUID userId = userService.getCurrentUser().getId();

        return workspaceRepo.findWorkspacesByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    // ---------------- DELETE ----------------
    public void deleteWorkspace(UUID workspaceId) {

        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        UUID currentUserId = userService.getCurrentUser().getId();

        if (!workspace.getCreatedBy().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this workspace");
        }

        workspaceRepo.delete(workspace);
    }

    private WorkspaceResponseDto mapToDto(Workspace workspace) {
        return new WorkspaceResponseDto(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getCreatedBy(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt(),
                workspace.getType(),
                workspace.getWorkspaceKey()
        );
    }
// invite a user
    @Transactional
    public void inviteAUser(UUID workspaceId, WorkspaceInvitationRequestDto dto) {

        // Check workspace exist
        Optional<Workspace> byId = workspaceRepo.findById(workspaceId);

        if (byId.isEmpty())
        {
            throw new ResourceNotFoundException("workspace not found...");
        }
        Workspace workspace=byId.get();
        //only admin can send invitation
        UUID currentUserId = userService.getCurrentUser().getId();


        boolean isAdmin = workspaceMemberRepository
                .existsByWorkspaceAndUserIdAndRole(workspace, currentUserId, WorkspaceRole.ADMIN);

        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can invite users");
        }
        Optional<User> invitedUser = userRepo.findByEmail(dto.getEmail());

        if (invitedUser.isEmpty()) {
            throw new ResourceNotFoundException("User with this email does not exist in the system");
        }
        boolean alreadyMember = workspaceMemberRepository
                .existsByWorkspaceAndUser(workspace, invitedUser);

        if (alreadyMember) {
            throw new IllegalStateException("User is already a member of this workspace");
        }


        //  Check existing invitation
        Optional<WorkspaceInvitation> exisyingWorkspace = workspaceInvitaionRepo.findByWorkspaceAndEmail(workspace, dto.getEmail());

        exisyingWorkspace.ifPresent(workspaceInvitaionRepo::delete);


        // 2️⃣ Generate invitation token
        String token = UUID.randomUUID().toString();
        WorkspaceInvitation workspaceInvitation=new WorkspaceInvitation();
        workspaceInvitation.setToken(token);
        workspaceInvitation.setRole(dto.getRole());
        workspaceInvitation.setEmail(dto.getEmail());
        workspaceInvitation.setWorkspace(workspace);
        workspaceInvitation.setExpiresAt(
                Instant.now().plus(24, ChronoUnit.HOURS)
        );
        workspaceInvitaionRepo.save(workspaceInvitation);
        String verificationUrl = "http://localhost:8585/auth/accept?token=" + token;
        // Send email
        emailService.sendInvitationTokenEmail(workspaceInvitation.getEmail(), verificationUrl);
    }

    @Transactional
    public void acceptInvitation(String token) {

        // 1️⃣ Find invitation
        WorkspaceInvitation invitation = (WorkspaceInvitation) workspaceInvitaionRepo.findByToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid invitation token")
                );

        // 2️⃣ Check if already accepted
        if (invitation.isAccepted()) {
            throw new IllegalStateException("Invitation already accepted");
        }

        // 3️⃣ Check expiry
        if (invitation.getExpiresAt() != null &&
                invitation.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Invitation has expired");
        }

        // 4️⃣ Find user by email
        User user = userRepo.findByEmail(invitation.getEmail())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User must register before accepting invitation"
                        )
                );

        UUID workspaceId = invitation.getWorkspace().getId();
        UUID userId = user.getId();

        // 5️⃣ Prevent duplicate membership
        if (memberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new IllegalStateException("User is already a workspace member");
        }

        // 6️⃣ Create workspace member
        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspace(invitation.getWorkspace());
        member.setUser(user);
        member.setRole(invitation.getRole());

        memberRepository.save(member);

        // 7️⃣ Mark invitation as accepted
        invitation.setAccepted(true);
        workspaceInvitaionRepo.save(invitation);
    }


    public List<WorkspaceMemberDto> getMembersOfWorkspace(UUID workspaceId) {
        Optional<Workspace> byId = workspaceRepo.findById(workspaceId);
        Workspace workspace=byId.get();
        return workspace.getWorkspaceMembers()
                .stream()
                .map(member -> new WorkspaceMemberDto(
                        member.getUser().getId(),
                        member.getUser().getFirstName(),
                        member.getUser().getEmail(),
                        member.getRole().name()
                ))
                .toList();
    }
}
