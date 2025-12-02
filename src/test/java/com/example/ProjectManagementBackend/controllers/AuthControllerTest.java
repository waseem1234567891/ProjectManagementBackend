package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.exceptions.EmailAlreadyExistException;
import com.example.ProjectManagementBackend.exceptions.PasswordInCorrectException;
import com.example.ProjectManagementBackend.exceptions.UserNotFoundException;
import com.example.ProjectManagementBackend.services.UserService;
import com.example.ProjectManagementBackend.util.JwtUtil;
import com.example.ProjectManagementBackend.dto.auth.LoginRequestDto;
import com.example.ProjectManagementBackend.dto.auth.RegisterationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    // -----------------------------
    // Test: REGISTER SUCCESS
    // -----------------------------
    @Test
    void testRegister_Success() throws Exception {
        when(userService.registerAUser(any(RegisterationDto.class)))
                .thenAnswer(invocation ->
                        ResponseEntity.status(201).body("User is created Successfully")
                );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john@gmail.com",
                                  "password": "tumbin1234",
                                  "role": "USER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().string("User is created Successfully"));
    }

    // -----------------------------
    // Test: REGISTER VALIDATION FAILURE
    // -----------------------------
    @Test
    void testRegister_ValidationFailure() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // Test: REGISTER - EMAIL ALREADY EXISTS
// -----------------------------
    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        // Mock UserService to throw EmailAlreadyExistException
        when(userService.registerAUser(any(RegisterationDto.class)))
                .thenThrow(new EmailAlreadyExistException("This Email Address Already Exist..."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "John",
                              "lastName": "Doe",
                              "email": "john@gmail.com",
                              "password": "123456",
                              "role": "USER"
                            }
                            """))
                .andExpect(status().isConflict()) // HTTP 409
                .andExpect(jsonPath("$.message").value("This Email Address Already Exist..."));
    }
    @Test
    void testRegister_WhitespaceFields() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "   ",
                          "lastName": "   ",
                          "email": "   ",
                          "password": "   ",
                          "role": "   "
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // -----------------------------
    // Test: LOGIN SUCCESS
    // -----------------------------
    @Test
    void testLogin_Success() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenAnswer(invocation ->
                        ResponseEntity.ok("Login Successful")
                );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@gmail.com",
                                  "password": "tumbin1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Login Successful"));
    }

    // -----------------------------
    // Test: LOGIN VALIDATION FAILURE
    // -----------------------------
    @Test
    void testLogin_ValidationFailure() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------



    // -----------------------------
// Test: LOGIN - USER NOT FOUND
// -----------------------------
    @Test
    void testLogin_UserNotFound() throws Exception {
        // Mock UserService to throw UserNotFoundException
        when(userService.login(any(LoginRequestDto.class)))
                .thenThrow(new UserNotFoundException("User Not Found With Email john@gmail.com"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "john@gmail.com",
                              "password": "123456"
                            }
                            """))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(jsonPath("$.message").value("User Not Found With Email john@gmail.com"));
    }

    @Test
    void testLogin_WhitespaceFields() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "   ",
                          "password": "   "
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

//  test: Password incorrect
@Test
void testLogin_InCorrectPassword() throws Exception {
    // Mock UserService to throw UserNotFoundException
    when(userService.login(any(LoginRequestDto.class)))
            .thenThrow(new PasswordInCorrectException("Wrong Password"));

    mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "email": "john@gmail.com",
                              "password": "123456"
                            }
                            """))
            .andExpect(status().isUnauthorized()) // HTTP 401
            .andExpect(jsonPath("$.message").value("Wrong Password"));
}



}
