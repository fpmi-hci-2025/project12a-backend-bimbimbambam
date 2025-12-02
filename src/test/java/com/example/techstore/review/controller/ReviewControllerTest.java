package com.example.techstore.review.controller;

import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.review.dto.ReviewDTO;
import com.example.techstore.review.dto.ReviewResponseDTO;
import com.example.techstore.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ReviewService reviewService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void addReview_Success() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(1L);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Good!");

        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(10L);

        when(reviewService.addReview(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/reviews")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void getProductReviews_Success() throws Exception {
        Page<ReviewResponseDTO> page = new PageImpl<>(Collections.emptyList());
        when(reviewService.getProductReviews(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/reviews/product/1"))
                .andExpect(status().isOk());
    }
}
