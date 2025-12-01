package com.example.techstore.catalog.controller;

import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.service.FavoriteService;
import com.example.techstore.common.util.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getFavorites(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            Pageable pageable
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId, pageable));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> toggleFavorite(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long userId = getUserIdFromToken(token);
        favoriteService.toggleFavorite(userId, productId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenUtils.getUserId(jwt);
    }
}