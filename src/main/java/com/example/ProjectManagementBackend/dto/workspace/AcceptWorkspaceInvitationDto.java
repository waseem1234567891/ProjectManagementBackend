package com.example.ProjectManagementBackend.dto.workspace;

import jakarta.validation.constraints.NotBlank;

public class AcceptWorkspaceInvitationDto {
    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

