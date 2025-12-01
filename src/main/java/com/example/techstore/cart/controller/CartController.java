package com.example.techstore.cart.controller;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.service.CartService;
import com.example.techstore.common.util.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @PatchMapping("/update")
    public ResponseEntity<CartDTO> updateQuantity(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(cartService.updateQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long productId
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenUtils.getUserId(jwt);
    }
}