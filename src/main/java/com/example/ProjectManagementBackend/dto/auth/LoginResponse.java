package com.example.ProjectManagementBackend.dto.auth;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private UUID userId;
    private String role;


    public LoginResponse(String token, UUID userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;

    }

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
