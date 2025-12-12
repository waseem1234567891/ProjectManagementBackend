package com.example.ProjectManagementBackend.dto.user;

import com.example.ProjectManagementBackend.models.User;

public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public UserProfileDto(User user) {
        this.firstName= user.getFirstName();
        this.lastName= user.getLastName();
        this.email= user.getEmail();
        this.role= user.getRole();
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
