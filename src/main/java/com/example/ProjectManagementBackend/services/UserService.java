package com.example.ProjectManagementBackend.services;



import com.example.ProjectManagementBackend.exceptions.EmailAlreadyExistException;
import com.example.ProjectManagementBackend.exceptions.PasswordInCorrectException;
import com.example.ProjectManagementBackend.exceptions.UserNotFoundException;
import com.example.ProjectManagementBackend.models.CustomUserDetail;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import com.example.ProjectManagementBackend.util.JwtUtil;
import com.example.ProjectManagementBackend.dto.auth.LoginRequestDto;
import com.example.ProjectManagementBackend.dto.auth.LoginResponse;
import com.example.ProjectManagementBackend.dto.auth.RegisterationDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//register a user
    public ResponseEntity<?> registerAUser(RegisterationDto registerationDto)
    {
        Optional<User> byEmail = userRepo.findByEmail(registerationDto.getEmail());
        if (byEmail.isPresent())
        {
            throw new EmailAlreadyExistException("This Email Address Already Exist...");
        }
        User user=new User();
        user.setFirstName(registerationDto.getFirstName());
        user.setLastName(registerationDto.getLastName());
        user.setEmail(registerationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerationDto.getPassword()));
        user.setRole("USER");
        userRepo.save(user);
        return ResponseEntity.status(201).body("User is created Successfully");
    }

    public ResponseEntity<?> login(@Valid LoginRequestDto loginRequestDto) {
        Optional<User> byEmail = userRepo.findByEmail(loginRequestDto.getEmail());
        if (byEmail.isEmpty())
        {
            throw new UserNotFoundException("User Not Found With Email "+loginRequestDto.getEmail());
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(), loginRequestDto.getPassword()
                    )
            );

            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();

            String token = jwtUtil.generateToken(
                    userDetails.getUsername(),
                    userDetails.getRole(),
                    userDetails.getId()
            );



            return ResponseEntity.ok(
                    new LoginResponse(token, userDetails.getId(),
                            userDetails.getRole())
            );

        } catch (BadCredentialsException e) {
            throw new PasswordInCorrectException("Wrong Password");
        }

    }
}
