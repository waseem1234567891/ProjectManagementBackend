package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.dto.user.UserProfileDto;
import com.example.ProjectManagementBackend.exceptions.EmailAlreadyExistException;
import com.example.ProjectManagementBackend.exceptions.EmailNotVerifiedException;
import com.example.ProjectManagementBackend.exceptions.PasswordInCorrectException;
import com.example.ProjectManagementBackend.exceptions.UserNotFoundException;
import com.example.ProjectManagementBackend.models.CustomUserDetail;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import com.example.ProjectManagementBackend.respositories.VerificationTokenRepository;
import com.example.ProjectManagementBackend.util.JwtUtil;
import com.example.ProjectManagementBackend.dto.auth.LoginRequestDto;
import com.example.ProjectManagementBackend.dto.auth.LoginResponse;
import com.example.ProjectManagementBackend.dto.auth.RegisterationDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
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

    @Mock
    private VerificationTokenRepository passwordResetTokenRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }



    @Test
    void testRegisterUser_Success() {
        RegisterationDto dto = new RegisterationDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@gmail.com");
        dto.setPassword("tumbin1234");


        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        ResponseEntity<?> response = userService.registerAUser(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("User is created Successfully", response.getBody());

        verify(userRepo).save(any(User.class));
        verify(passwordEncoder).encode("tumbin1234");
        verify(emailService).sendVerificationEmail(eq("john@gmail.com"), anyString());
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

    @Test
    void testLogin_EmailNotVerified() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassword("Pass123");

        // Mock user (not verified)
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedpass");
        user.setEnabled(false);  // 👈 KEY: user is NOT verified
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));

        // Simulate Spring Security blocking login
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User is disabled"));

        assertThrows(EmailNotVerifiedException.class, () -> userService.login(dto));
    }

    @Test
    void getUserProfile_shouldReturnUserProfile() {

        User user = new User();
        user.setEmail("test@gmail.com");

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        "test@gmail.com", "pass", new ArrayList<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.getUserProfile();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserProfileDto);
    }

    @Test
    void getCurrentUser_shouldThrowUserNotFoundException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("missing@gmail.com");
        when(userRepo.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getCurrentUser());
    }

    @Test
    void getCurrentUser_shouldReturnUser_whenPrincipalIsString() {

        User user = new User();
        user.setEmail("test@gmail.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("test@gmail.com");
        when(userRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void getCurrentUser_shouldThrowException_whenAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser());
    }

    @Test
    void getCurrentUser_shouldThrowException_whenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser());
    }
    @Test
    void getCurrentUser_shouldThrowException_whenAuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> userService.getCurrentUser());
    }



}
