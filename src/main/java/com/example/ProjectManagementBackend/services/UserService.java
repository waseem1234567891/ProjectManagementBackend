package com.example.ProjectManagementBackend.services;



import com.example.ProjectManagementBackend.dto.auth.AuthResponse;
import com.example.ProjectManagementBackend.dto.user.UserProfileDto;
import com.example.ProjectManagementBackend.exceptions.*;
import com.example.ProjectManagementBackend.models.*;
import com.example.ProjectManagementBackend.respositories.PasswordResetTokenRepo;
import com.example.ProjectManagementBackend.respositories.RefreshTokenRepository;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import com.example.ProjectManagementBackend.respositories.VerificationTokenRepository;
import com.example.ProjectManagementBackend.util.JwtUtil;
import com.example.ProjectManagementBackend.dto.auth.LoginRequestDto;
import com.example.ProjectManagementBackend.dto.auth.LoginResponse;
import com.example.ProjectManagementBackend.dto.auth.RegisterationDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
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

        // 2️⃣ Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        String verificationUrl = "http://localhost:8585/auth/verify?token=" + token;
        // Send email
        emailService.sendVerificationEmail(user.getEmail(), verificationUrl);

        return ResponseEntity.status(201).body("User is created Successfully");
    }
 //user login
    public ResponseEntity<?> login(@Valid LoginRequestDto loginRequestDto) {
        Optional<User> byEmail = userRepo.findByEmail(loginRequestDto.getEmail());
        User user;
        if (byEmail.isEmpty())
        {
            throw new UserNotFoundException("User Not Found With Email "+loginRequestDto.getEmail());
        }
        else {
            user=byEmail.get();
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

            // 3. Generate REFRESH TOKEN
            RefreshToken refreshToken =refreshTokenService.createRefreshToken(user);

            return ResponseEntity.ok(
                    new LoginResponse(token,refreshToken.getToken(), userDetails.getId(),
                            userDetails.getRole())
            );

        } catch (DisabledException e) {

            // 👈 email not verified
            throw new EmailNotVerifiedException("Please verify your email before logging in.");

        }  catch (BadCredentialsException e) {
            throw new PasswordInCorrectException("Wrong Password");
        }

    }

//verify user email
    public ResponseEntity<String> verifyUserEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepo.save(user);

        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email verified successfully!");
    }

    // Send password reset link
    public ResponseEntity<?> sendResetPasswordEmail(String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Email not found");
        }

        User user = userOpt.get();

        // Delete any existing password reset tokens for this user
        passwordResetTokenRepo.findByUser(user)
                .forEach(passwordResetTokenRepo::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(30)
        );
        passwordResetTokenRepo.save(resetToken);

        // Construct the reset URL
        String passwordResetUrl = "http://localhost:3000/reset-password?token=" + token;

        // Send the reset email
        emailService.sendPasswordResetEmail(email, passwordResetUrl);

        return ResponseEntity.ok("Password reset email sent");
    }


    //validate password reset token
    public ResponseEntity<?> validatePasswordResetToken(String token)
    {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired token");
        }

        return ResponseEntity.ok("Token valid");
    }
    //reset user password
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired token");
        }
        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        passwordResetTokenRepo.delete(resetToken);
        return ResponseEntity.ok("Password updated successfully ");
    }

    //get user profile
    public ResponseEntity<?> getUserProfile() {

        User currentUser = getCurrentUser();
        return ResponseEntity.ok(new UserProfileDto(currentUser));
    }
// get current login user
public User getCurrentUser()
{
    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    if(authentication==null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser"))
    {
        throw new AccessDeniedException("Unauthenticated");
    }
    Object principle=authentication.getPrincipal();
    String email;
    if (principle instanceof UserDetails)
    {
        email=((UserDetails) principle).getUsername();
    }else {
        email=principle.toString();
    }
    Optional<User> byEmail = userRepo.findByEmail(email);
    if (byEmail.isPresent())
    {
        return byEmail.get();
    }else {
        throw new UserNotFoundException("user not found");
    }
    }

    public ResponseEntity<?> updateUserProfile(UserProfileDto dto) {
        User currentUser = getCurrentUser();

        if (dto.getFirstName() != null) {
            currentUser.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            currentUser.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            currentUser.setEmail(dto.getEmail());
        }

        userRepo.save(currentUser);

        return ResponseEntity.ok(new UserProfileDto(currentUser));
    }


    public AuthResponse refreshToken(String refreshTokenValue) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isExpired() || token.isRevoked()) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getId()
        );

        // 🔥 create new refresh token
        //RefreshToken newRefreshToken = new RefreshToken();
        //newRefreshToken.setToken(UUID.randomUUID().toString());
       // newRefreshToken.setUser(user);
       // newRefreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
       // newRefreshToken.setRevoked(false);

        //refreshTokenRepository.save(newRefreshToken);
        System.out.println("refreshed token............."+user.getFirstName());
        return new AuthResponse(
                newAccessToken,
                refreshTokenValue,
                user.getId(),
                user.getRole()
        );
    }


}
