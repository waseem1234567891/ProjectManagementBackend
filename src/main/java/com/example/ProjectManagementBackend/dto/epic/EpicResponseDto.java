package com.example.ProjectManagementBackend.dto.epic;

import com.example.ProjectManagementBackend.models.Epic;

import java.util.UUID;

public class EpicResponseDto {
    private UUID id;

    private String name;

    private String description;

    private String colour;

    public EpicResponseDto() {
    }

    public EpicResponseDto(UUID id, String name, String description,String colour) {
        this.id=id;
        this.name=name;
        this.description=description;
        this.colour=colour;
    }

    public EpicResponseDto(Epic epic) {
        this.id=epic.getId();
        this.name=epic.getName();
        this.description=epic.getDescription();
        this.colour=epic.getColour();

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
