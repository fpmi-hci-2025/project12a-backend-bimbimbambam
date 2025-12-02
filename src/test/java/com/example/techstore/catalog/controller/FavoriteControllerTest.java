package com.example.techstore.catalog.controller;

import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.service.FavoriteService;
import com.example.techstore.common.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private FavoriteService favoriteService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void getFavorites_Success() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(Collections.emptyList());
        when(favoriteService.getUserFavorites(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/favorites")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void toggleFavorite_Success() throws Exception {
        mockMvc.perform(post("/api/v1/favorites/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}