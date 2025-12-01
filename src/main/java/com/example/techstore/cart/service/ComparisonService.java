package com.example.techstore.cart.service;

import com.example.techstore.catalog.dto.ProductDetailsDTO;

import java.util.List;

public interface ComparisonService {
    List<ProductDetailsDTO> getComparison(Long userId);
    List<ProductDetailsDTO> toggleProduct(Long userId, Long productId);
    void clearComparison(Long userId);
}
