package com.example.techstore.review.controller;

import com.example.techstore.common.util.ErrorsUtil;
import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.review.dto.ReviewDTO;
import com.example.techstore.review.dto.ReviewResponseDTO;
import com.example.techstore.review.service.ReviewService;
import com.example.techstore.review.service.ReviewServiceImpl;
import com.example.techstore.review.utils.ReviewException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @RequestBody @Valid ReviewDTO reviewDTO,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            String errorMsg = ErrorsUtil.getErrorMsg(bindingResult);
            throw new ReviewException(errorMsg);
        }

        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(reviewService.addReview(userId, reviewDTO));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId
    ) {
        Long userId = getUserIdFromToken(token);
        List<String> roles = jwtTokenUtils.getRoles(token.replace("Bearer ", ""));
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        reviewService.deleteReview(userId, reviewId, isAdmin);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromToken(String token) {
        return jwtTokenUtils.getUserId(token.replace("Bearer ", ""));
    }
}