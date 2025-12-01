package com.example.techstore.review.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long productId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}