package com.example.techstore.cart.service;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.dto.CartItem;
import com.example.techstore.cart.util.CartException;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CatalogService catalogService;

    private static final String CART_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7);

    @Override
    public CartDTO getCart(Long userId) {
        String key = getCartKey(userId);
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(key);

        CartDTO cart = new CartDTO();
        if (items != null) {
            cart.setItems(items);
            cart.calculateTotals();
        }
        return cart;
    }

    @Override
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new CartException("The quantity of the product must be more than 0");
        }

        ProductDetailsDTO product = catalogService.getProductById(productId);

        String key = getCartKey(userId);
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(key);
        if (items == null) {
            items = new ArrayList<>();
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(
                    productId,
                    product.getTitle(),
                    quantity,
                    product.getPrice(),
                    product.getImageUrl()
            );
            items.add(newItem);
        }

        redisTemplate.opsForValue().set(key, items, CART_TTL);

        return getCart(userId);
    }

    @Override
    public CartDTO removeFromCart(Long userId, Long productId) {
        String key = getCartKey(userId);
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(key);

        if (items != null) {
            items.removeIf(item -> item.getProductId().equals(productId));
            redisTemplate.opsForValue().set(key, items, CART_TTL);
        }

        return getCart(userId);
    }

    @Override
    public void clearCart(Long userId) {
        redisTemplate.delete(getCartKey(userId));
    }

    @Override
    public CartDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            return removeFromCart(userId, productId);
        }

        String key = getCartKey(userId);
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(key);

        if (items != null) {
            items.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));

            redisTemplate.opsForValue().set(key, items, CART_TTL);
        }

        return getCart(userId);
    }

    private String getCartKey(Long userId) {
        return CART_PREFIX + userId;
    }
}