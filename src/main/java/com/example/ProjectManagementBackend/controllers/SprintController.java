package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.sprint.CreateSprintRequestDto;
import com.example.ProjectManagementBackend.dto.sprint.EditSprintDto;
import com.example.ProjectManagementBackend.dto.sprint.SprintResponseDto;
import com.example.ProjectManagementBackend.models.Sprint;
import com.example.ProjectManagementBackend.services.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspace/{workspaceId}/sprints")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    // POST /workspace/{workspaceId}/sprints — create a sprint
    @PostMapping
    public ResponseEntity<SprintResponseDto> createSprint(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateSprintRequestDto dto) {
        return ResponseEntity.ok(sprintService.createSprint(workspaceId, dto));
    }

    // GET /workspace/{workspaceId}/sprints — list all sprints
    @GetMapping
    public ResponseEntity<List<SprintResponseDto>> getSprints(@PathVariable UUID workspaceId) {
        return ResponseEntity.ok(sprintService.getSprintsByWorkspace(workspaceId));
    }

    // GET /workspace/{workspaceId}/sprints/active — get active sprint
    @GetMapping("/active")
    public ResponseEntity<SprintResponseDto> getActiveSprint(@PathVariable UUID workspaceId) {
        return ResponseEntity.ok(sprintService.getActiveSprint(workspaceId));
    }

    // PATCH /workspace/{workspaceId}/sprints/{sprintId}/start — start a sprint
    @PatchMapping("/{sprintId}/start")
    public ResponseEntity<SprintResponseDto> startSprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId) {
        return ResponseEntity.ok(sprintService.startSprint(sprintId));
    }

    // PATCH /workspace/{workspaceId}/sprints/{sprintId}/complete — complete a sprint
    @PatchMapping("/{sprintId}/complete")
    public ResponseEntity<SprintResponseDto> completeSprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId) {
        return ResponseEntity.ok(sprintService.completeSprint(sprintId));
    }

    // DELETE /workspace/{workspaceId}/sprints/{sprintId}
    @DeleteMapping("/{sprintId}")
    public ResponseEntity<Void> deleteSprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId) {
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.noContent().build();
    }
    // update /workspace/{workspaceId}/sprints/{sprintId}
    @PatchMapping("/{sprintId}")
    public ResponseEntity<SprintResponseDto> updateSprint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId,
            @Valid @RequestBody EditSprintDto dto
    ) {
        Sprint updatedSprint = sprintService.updateSprint(workspaceId, sprintId, dto);
        SprintResponseDto sprintResponseDto=new SprintResponseDto(updatedSprint);

        return ResponseEntity.ok(sprintResponseDto);
    }

    // GET /workspace/{workspaceId}/sprints/{sprintId}
    @GetMapping ("/{sprintId}")
    public ResponseEntity<SprintResponseDto> getSprintById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID sprintId
    ) {
        Sprint updatedSprint = sprintService.getSprintById(workspaceId,sprintId);
        SprintResponseDto sprintResponseDto=new SprintResponseDto(updatedSprint);

        return ResponseEntity.ok(sprintResponseDto);
    }
}
