package com.example.ProjectManagementBackend.controllers;


import com.example.ProjectManagementBackend.dto.workspace.*;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import com.example.ProjectManagementBackend.services.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private UserRepo userRepo;


    //invite a user for join a work space
    @PostMapping("/{workspaceId}/invitaion")
    public ResponseEntity<?> inviteAMember(@PathVariable UUID workspaceId, @Valid @RequestBody WorkspaceInvitationRequestDto dto)
    {
        workspaceService.inviteAUser(workspaceId,dto);
        return ResponseEntity.ok("invitation email has been sent to "+dto.getEmail()
        );
    }
    //get a workspace
    @GetMapping("/{workspaceId}")
    public ResponseEntity<?> getworkspace(@PathVariable UUID workspaceId)
    {
        WorkspaceResponseDto workspaceById = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspaceById);
    }

    @GetMapping("my")
    public ResponseEntity<?> getAllWorkspaces()
    {
        List<WorkspaceResponseDto> myWorkspaces = workspaceService.getMyWorkspaces();
        return ResponseEntity.ok(myWorkspaces);
    }

    // create new workspace
    @PostMapping("/create-workspace")
    public ResponseEntity<?> createWorkspace(@Valid @RequestBody CreateWorkspaceRequestDto dto)
    {
        return workspaceService.createWorkspace(dto);
    }

    //update workspace
    @PatchMapping("/update-workspace/{workspaceId}")
    public ResponseEntity<?> updateWorkspace(@PathVariable UUID workspaceId, @Valid @RequestBody UpdateWorkspaceRequestDto dto)
    {
        WorkspaceResponseDto workspaceResponseDto = workspaceService.updateWorkspace(workspaceId, dto);
        return  ResponseEntity.ok(workspaceResponseDto);
    }

    //get workspace by workspace id
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> getWorkspaceById(@PathVariable UUID workspaceId)
    {
        WorkspaceResponseDto workspaceById = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspaceById);
    }

    //delete workspace by workspace id

    @DeleteMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> deleteWorkspaceByWorkspaceId(@PathVariable UUID workspaceId)
    {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build(); // 204
    }

    //get my all workspaces

    @GetMapping("/workspaces")
    public ResponseEntity<?> getMyworkspaces()
    {
        List<WorkspaceResponseDto> myWorkspaces = workspaceService.getMyWorkspaces();
        return ResponseEntity.ok(myWorkspaces);
    }

    @GetMapping("/{workspaceId}/members")
    public List<WorkspaceMemberDto> getMembersOfWorkspace(@PathVariable UUID workspaceId)
    {
       return workspaceService.getMembersOfWorkspace(workspaceId);
    }


}
