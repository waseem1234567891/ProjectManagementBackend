package com.example.ProjectManagementBackend.controllers;

import com.example.ProjectManagementBackend.dto.user.UserProfileDto;
import com.example.ProjectManagementBackend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // Use Spring Boot's ObjectMapper

    @Test
    @WithMockUser(username = "john@gmail.com", roles = "USER")
    void getUserProfile_Success() throws Exception {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@gmail.com");

        // Mock service response
        doReturn(ResponseEntity.ok(dto))
                .when(userService).getUserProfile();

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }

    @Test
    @WithMockUser(username = "john@gmail.com", roles = "USER")
    void updateUserProfile_Success() throws Exception {
        UserProfileDto inputDto = new UserProfileDto();
        inputDto.setFirstName("John");
        inputDto.setLastName("Doe");

        UserProfileDto returnedDto = new UserProfileDto();
        returnedDto.setFirstName("John");
        returnedDto.setLastName("Doe");
        returnedDto.setEmail("john@gmail.com");

        // Mock service response for any UserProfileDto
        doReturn(ResponseEntity.ok(returnedDto))
                .when(userService).updateUserProfile(any(UserProfileDto.class));

        mockMvc.perform(patch("/user/update-profile")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }
}
