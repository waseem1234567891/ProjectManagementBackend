package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.workspace.WorkspaceMemberDto;
import com.example.ProjectManagementBackend.exceptions.UserNotFoundException;
import com.example.ProjectManagementBackend.exceptions.WorkspaceMemberNotFound;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.models.WorkspaceMember;
import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;
import com.example.ProjectManagementBackend.respositories.WorkspaceMemberRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkspaceMemberService {

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Autowired
    private UserService userService;
    public ResponseEntity<?> removeMember(UUID workspaceId, UUID memberId) {

        User currentUser=userService.getCurrentUser();

        // Check if current user is an OWNER of this workspace
        WorkspaceMember currentMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not a member of this workspace."));
       //only Owner or Admin can remove member
        if (currentMember.getRole() != WorkspaceRole.OWNER && currentMember.getRole()!=WorkspaceRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only the workspace owner or Admin can remove a member.");
        }

        Optional<WorkspaceMember> workspaceMemberOtional = workspaceMemberRepository.findByWorkspaceIdAndId(workspaceId, memberId);
        WorkspaceMember member;
        if (workspaceMemberOtional.isPresent())
        {
            member=workspaceMemberOtional.get();
        }
        else {
            throw new UserNotFoundException("Workspace Member Not Found with Id "+memberId);
        }
        workspaceMemberRepository.delete(member);

        return ResponseEntity.ok("Member remove successfully");

    }

    public List<WorkspaceMemberDto> getMembersOfWorkspace(UUID workspaceId) {
        List<WorkspaceMember> allByWorkspaceId = workspaceMemberRepository.findAllByWorkspaceId(workspaceId);
    return    allByWorkspaceId.stream().map(member -> new WorkspaceMemberDto(
                       member.getId(),
                       member.getUser().getId(),
                       member.getUser().getFirstName(),
                       member.getUser().getEmail(),
                       member.getRole().name())
               ).toList();
    }

    @Transactional
    public ResponseEntity<?> changeMemberRole(
            UUID workspaceId,
            UUID memberId,
            WorkspaceRole newRole) {

        // Logged-in user
        User currentUser = userService.getCurrentUser();

        // Check if current user is an OWNER of this workspace
        WorkspaceMember currentMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not a member of this workspace."));

        if (currentMember.getRole() != WorkspaceRole.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only the workspace owner can change member roles.");
        }

        // Find the target member
        WorkspaceMember targetMember = workspaceMemberRepository
                .findByWorkspaceIdAndId(workspaceId, memberId)
                .orElseThrow(() -> new RuntimeException("Member not found."));

        // Optional: prevent changing owner's role
        if (targetMember.getRole() == WorkspaceRole.OWNER) {
            return ResponseEntity.badRequest()
                    .body("Owner's role cannot be changed.");
        }

        targetMember.setRole(newRole);
        workspaceMemberRepository.save(targetMember);

        return ResponseEntity.ok("Member role updated successfully.");
    }

    public WorkspaceMemberDto getMember(UUID workspaceId, UUID memberId) {
        Optional<WorkspaceMember> workspaceMemberOptional = workspaceMemberRepository.findByWorkspaceIdAndId(workspaceId, memberId);
         if (workspaceMemberOptional.isEmpty())
         {
             throw new WorkspaceMemberNotFound("workspace Member not found with id "+memberId);
         }
        WorkspaceMember member = workspaceMemberOptional.get();
         return new WorkspaceMemberDto(member.getId(),
                 member.getUser().getId(),
                 member.getUser().getFirstName(),
                 member.getUser().getEmail(),
                 member.getRole().name());
    }
// current login user can leave the workspace
    public ResponseEntity<?> leaveWorkspace(UUID workspaceId) {
        User currentUser=userService.getCurrentUser();
        Optional<WorkspaceMember> workspaceMemberOptional = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId,currentUser.getId());
      if(workspaceMemberOptional.isEmpty())
      {
          throw new WorkspaceMemberNotFound("workspace member not found with id "+currentUser.getId());
      }
        WorkspaceMember member = workspaceMemberOptional.get();
      if (member.getRole()==WorkspaceRole.OWNER)
      {
          throw new RuntimeException(
                  "You must transfer ownership before leaving the workspace.");
      }
      workspaceMemberRepository.delete(member);
      return ResponseEntity.ok("you leave the workspace successfully");
    }

    public ResponseEntity<?> transferOwnership(UUID workspaceId, UUID memberId) {
        Optional<WorkspaceMember> workspaceMemberOtional = workspaceMemberRepository.findByWorkspaceIdAndId(workspaceId, memberId);
    if(workspaceMemberOtional.isEmpty())
    {
        throw new WorkspaceMemberNotFound("workspace member not found with id "+memberId);
    }
    WorkspaceMember newOwner=workspaceMemberOtional.get();
    User currentUser=userService.getCurrentUser();
        Optional<WorkspaceMember> byWorkspaceIdAndUserId = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUser.getId());
        if (byWorkspaceIdAndUserId.isEmpty())
        {
            throw new WorkspaceMemberNotFound("you are not member of workspace");
        }
        WorkspaceMember currentOwner = byWorkspaceIdAndUserId.get();
        if (currentOwner.getRole()!=WorkspaceRole.OWNER)
        {
            throw new WorkspaceMemberNotFound("you are not owner of workspace. only owner can transfer Owvership");
        }
        // Prevent transferring ownership to yourself
        if (currentOwner.getId().equals(newOwner.getId())) {
            throw new RuntimeException("You are already the owner.");
        }
        newOwner.setRole(WorkspaceRole.OWNER);
        currentOwner.setRole(WorkspaceRole.ADMIN);
        workspaceMemberRepository.saveAll(List.of(currentOwner, newOwner));

        return ResponseEntity.ok("Workspace ownership transferred successfully.");
    }

//    public ResponseEntity<?> changeMemberStatus(UUID workspaceId, UUID memberId, boolean active) {
//    }
}
