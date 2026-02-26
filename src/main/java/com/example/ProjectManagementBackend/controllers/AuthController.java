package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.workspace.AcceptWorkspaceInvitationDto;
import com.example.ProjectManagementBackend.respositories.VerificationTokenRepository;
import com.example.ProjectManagementBackend.services.UserService;
import com.example.ProjectManagementBackend.dto.auth.LoginRequestDto;
import com.example.ProjectManagementBackend.dto.auth.RegisterationDto;
import com.example.ProjectManagementBackend.services.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterationDto registerDto)
    {
    return userService.registerAUser(registerDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
    {
    return userService.login(loginRequestDto);
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {

      return   userService.verifyUserEmail(token);

    }
    @PostMapping("/reset-password-email")
    public ResponseEntity<?> sendPasswordResetToken(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        return userService.sendResetPasswordEmail(email);
    }


    //validate password reset token
    @GetMapping("/reset-password")
    public ResponseEntity<?> validatePasswordResetToken(@RequestParam String token)
    {
        return userService.validatePasswordResetToken(token);
    }
    //reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,@RequestParam String newPassword)
    {
        return userService.resetPassword(token,newPassword);
    }
//accept invitation
    @GetMapping("/accept")
    public ResponseEntity<String> acceptInvitation(
            @RequestParam String token
    ) {
        workspaceService.acceptInvitation(token);
        return ResponseEntity.ok("Invitation accepted successfully");
    }


}
