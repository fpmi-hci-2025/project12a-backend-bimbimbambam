package com.example.techstore.cart.service;

import com.example.techstore.cart.dto.CartDTO;

public interface CartService {
    CartDTO getCart(Long userId);
    CartDTO addToCart(Long userId, Long productId, Integer quantity);
    CartDTO removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
    CartDTO updateQuantity(Long userId, Long productId, Integer quantity);
}
