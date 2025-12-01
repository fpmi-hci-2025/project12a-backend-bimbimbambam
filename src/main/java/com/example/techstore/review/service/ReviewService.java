package com.example.techstore.review.service;

import com.example.techstore.catalog.model.Product;
import com.example.techstore.review.dto.ReviewDTO;
import com.example.techstore.review.dto.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponseDTO addReview(Long userId, ReviewDTO reviewDTO);
    Page<ReviewResponseDTO> getProductReviews(Long productId, Pageable pageable);
    void deleteReview(Long userId, Long reviewId, boolean isAdmin);
}
