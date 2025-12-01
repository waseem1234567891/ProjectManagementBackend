package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.services.UserService;
import dto.auth.LoginRequestDto;
import dto.auth.RegisterationDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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



}
