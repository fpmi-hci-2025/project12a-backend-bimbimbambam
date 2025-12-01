package com.example.techstore.cart.controller;

import com.example.techstore.cart.service.ComparisonService;
import com.example.techstore.cart.service.ComparisonServiceImpl;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.common.util.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comparison")
@RequiredArgsConstructor
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<List<ProductDetailsDTO>> getComparison(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(comparisonService.getComparison(userId));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<List<ProductDetailsDTO>> toggleProduct(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(comparisonService.toggleProduct(userId, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearComparison(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        comparisonService.clearComparison(userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenUtils.getUserId(jwt);
    }
}