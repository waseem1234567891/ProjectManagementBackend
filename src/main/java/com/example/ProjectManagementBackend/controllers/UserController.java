package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.user.UserProfileDto;
import com.example.ProjectManagementBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile()
    {
      return   userService.getUserProfile();
    }
    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileDto userProfileDto)
    {
        return userService.updateUserProfile(userProfileDto);
    }
}
