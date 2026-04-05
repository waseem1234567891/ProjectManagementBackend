package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.epic.EpicResponseDto;
import com.example.ProjectManagementBackend.models.Epic;
import com.example.ProjectManagementBackend.services.EpicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/workspace/{workspaceId}/epics")
public class EpicController {

    private final EpicService epicService;

    public EpicController(EpicService epicService) {
        this.epicService = epicService;
    }

    @PostMapping
    public ResponseEntity<?> createEpic(
            @PathVariable UUID workspaceId,
            @RequestBody Map<String, String> body
    ) {
        EpicResponseDto epic = epicService.createEpic(
                workspaceId,
                body.get("name"),
                body.get("description"),
                body.get("colour")
        );
        return  ResponseEntity.ok(epic);
    }

    @GetMapping
    public List<EpicResponseDto> getEpics(@PathVariable UUID workspaceId) {

        return epicService.getEpics(workspaceId);
    }
}
