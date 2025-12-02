package com.example.techstore.cart.controller;

import com.example.techstore.cart.service.ComparisonService;
import com.example.techstore.common.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComparisonController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComparisonControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ComparisonService comparisonService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void getComparison_Success() throws Exception {
        when(comparisonService.getComparison(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/comparison")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void toggleProduct_Success() throws Exception {
        when(comparisonService.toggleProduct(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/v1/comparison/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void clearComparison_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/comparison/clear")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}