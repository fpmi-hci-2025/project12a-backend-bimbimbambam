package com.example.techstore.cart.service;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.dto.CartItem;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOperations;
    @Mock private CatalogService catalogService;

    @InjectMocks
    private CartServiceImpl cartService;

    private final Long USER_ID = 1L;
    private final String CART_KEY = "cart:" + USER_ID;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getCart_Empty() {
        when(valueOperations.get(CART_KEY)).thenReturn(null);

        CartDTO cart = cartService.getCart(USER_ID);

        assertNotNull(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
    }

    @Test
    void addToCart_NewItem() {
        Long productId = 10L;
        ProductDetailsDTO product = new ProductDetailsDTO();
        product.setId(productId);
        product.setTitle("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));

        when(catalogService.getProductById(productId)).thenReturn(product);
        when(valueOperations.get(CART_KEY)).thenReturn(new ArrayList<CartItem>());

        CartDTO result = cartService.addToCart(USER_ID, productId, 1);

        verify(valueOperations).set(eq(CART_KEY), anyList(), any());
    }

    @Test
    void updateQuantity_RemoveIfZero() {
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(10L, "Item", 5, BigDecimal.TEN, "img"));

        when(valueOperations.get(CART_KEY)).thenReturn(items);

        cartService.updateQuantity(USER_ID, 10L, 0);

        verify(valueOperations).set(eq(CART_KEY), argThat(list -> ((List)list).isEmpty()), any());
    }

    @Test
    void clearCart_Success() {
        cartService.clearCart(USER_ID);
        verify(redisTemplate).delete(CART_KEY);
    }
}