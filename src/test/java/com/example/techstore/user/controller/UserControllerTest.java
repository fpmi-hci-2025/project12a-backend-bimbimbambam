package com.example.techstore.user.controller;

import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.user.dto.SaveUserDTO;
import com.example.techstore.user.dto.UserDTO;
import com.example.techstore.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void getUserProfile_Success() throws Exception {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");

        when(jwtTokenUtils.getUserId(any())).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_Success() throws Exception {
        Long userId = 1L;
        SaveUserDTO saveDto = new SaveUserDTO();
        saveDto.setUsername("newname");
        saveDto.setEmail("new@mail.com");
        saveDto.setPassword("pass");
        saveDto.setConfirmPassword("pass");

        UserDTO updatedUser = new UserDTO();
        updatedUser.setUsername("newname");

        when(userService.updateUserById(eq(userId), any(), any())).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newname"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", 1L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}