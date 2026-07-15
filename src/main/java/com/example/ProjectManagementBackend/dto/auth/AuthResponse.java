package com.example.ProjectManagementBackend.dto.auth;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private String refreshToken;

    private UUID userId;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, UUID userId, String role) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.role = role;
    }

    // getters & setters


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
