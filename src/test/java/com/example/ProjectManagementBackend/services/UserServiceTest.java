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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUser_Success() {
        RegisterationDto dto = new RegisterationDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@gmail.com");
        dto.setPassword("tumbin1234");


        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<?> response = userService.registerAUser(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("User is created Successfully", response.getBody());

        verify(userRepo).save(any(User.class));
        verify(passwordEncoder).encode("tumbin1234");
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        RegisterationDto dto = new RegisterationDto();
        dto.setEmail("test@gmail.com");

        User user = new User();
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService.registerAUser(dto));
    }

    @Test
    void testLogin_Success() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("pass");

        User user = new User();
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        CustomUserDetail customDetails = mock(CustomUserDetail.class);

        when(authentication.getPrincipal()).thenReturn(customDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(customDetails.getUsername()).thenReturn("test@gmail.com");
        when(customDetails.getRole()).thenReturn("USER");
        when(customDetails.getId()).thenReturn(1L);

        when(jwtUtil.generateToken("test@gmail.com", "USER", 1L))
                .thenReturn("mocked_jwt");

        ResponseEntity<?> response = userService.login(dto);
        LoginResponse loginResponse = (LoginResponse) response.getBody();

        assertNotNull(loginResponse);
        assertEquals("mocked_jwt", loginResponse.getToken());
        assertEquals(1L, loginResponse.getUserId());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("pass");

        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.login(dto));
    }
    @Test
    void testLogin_WrongPassword() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("wrongPass");

        User user = new User();
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(PasswordInCorrectException.class, () -> userService.login(dto));
    }




}
