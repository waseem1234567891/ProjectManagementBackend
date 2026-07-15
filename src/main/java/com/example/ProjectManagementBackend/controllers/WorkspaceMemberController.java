package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.workspace.ChangeRoleRequest;
import com.example.ProjectManagementBackend.dto.workspace.WorkspaceMemberDto;
import com.example.ProjectManagementBackend.models.enums.WorkspaceRole;
import com.example.ProjectManagementBackend.services.WorkspaceMemberService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
public class WorkspaceMemberController {

    @Autowired
    private WorkspaceMemberService workspaceMemberService;

    // Get all members of a workspace
    @GetMapping("/{workspaceId}")
    public List<WorkspaceMemberDto> getMembersOfWorkspace(
            @PathVariable UUID workspaceId) {

        return workspaceMemberService.getMembersOfWorkspace(workspaceId);
    }

    // Remove a member
    @DeleteMapping("/{workspaceId}/{memberId}")
    public ResponseEntity<?> removeMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) {

        return workspaceMemberService.removeMember(workspaceId, memberId);
    }

    // Change member role
    @PatchMapping("/{workspaceId}/{memberId}/role")
    public ResponseEntity<?> changeMemberRole(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId,
            @RequestBody ChangeRoleRequest request) {

        return workspaceMemberService.changeMemberRole(workspaceId, memberId, request.getRole());
    }

    // Get a single member
    @GetMapping("/{workspaceId}/{memberId}")
    public WorkspaceMemberDto getMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) {

        return workspaceMemberService.getMember(workspaceId, memberId);
    }

    // Leave workspace
    @DeleteMapping("/{workspaceId}//leave")
    public ResponseEntity<?> leaveWorkspace(
            @PathVariable UUID workspaceId) {

        return workspaceMemberService.leaveWorkspace(workspaceId);
    }

    // Transfer ownership
    @PatchMapping("/{workspaceId}/transfer-owner/{memberId}")
    public ResponseEntity<?> transferOwnership(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) {

        return workspaceMemberService.transferOwnership(workspaceId, memberId);
    }

//    // Deactivate/activate member
//    @PatchMapping("/{workspaceId}/{memberId}/status")
//    public ResponseEntity<?> changeMemberStatus(
//            @PathVariable UUID workspaceId,
//            @PathVariable UUID memberId,
//            @RequestParam boolean active) {
//
//        return workspaceMemberService.changeMemberStatus(workspaceId, memberId, active);
//    }
}