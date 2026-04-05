package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.epic.EpicResponseDto;
import com.example.ProjectManagementBackend.models.Epic;
import com.example.ProjectManagementBackend.models.Workspace;
import com.example.ProjectManagementBackend.respositories.EpicRepository;
import com.example.ProjectManagementBackend.respositories.WorkspaceRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EpicService {

    private final EpicRepository epicRepo;
    private final WorkspaceRepo workspaceRepo;

    public EpicService(EpicRepository epicRepo, WorkspaceRepo workspaceRepo) {
        this.epicRepo = epicRepo;
        this.workspaceRepo = workspaceRepo;
    }

    public EpicResponseDto createEpic(UUID workspaceId, String name, String description,String colour) {
        Workspace workspace = workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        epic.setWorkspace(workspace);
        epic.setColour(colour);
        Epic save = epicRepo.save(epic);
        EpicResponseDto dto=new EpicResponseDto();
        dto.setId( save.getId());
        dto.setName( save.getName());
        dto.setDescription( save.getDescription());
        return dto;
    }

    public List<EpicResponseDto> getEpics(UUID workspaceId) {
        List<Epic> byWorkspaceId = epicRepo.findByWorkspaceId(workspaceId);
        List<EpicResponseDto> list = byWorkspaceId.stream().map(e -> new EpicResponseDto(e.getId(), e.getName(), e.getDescription(),e.getColour())).toList();
    return list;
    }
}
