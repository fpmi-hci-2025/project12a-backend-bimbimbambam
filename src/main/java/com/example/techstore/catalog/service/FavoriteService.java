package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    void toggleFavorite(Long userId, Long productId);
    Page<ProductDTO> getUserFavorites(Long userId, Pageable pageable);
}
