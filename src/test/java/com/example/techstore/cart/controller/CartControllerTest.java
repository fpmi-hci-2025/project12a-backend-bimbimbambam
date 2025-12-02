package com.example.techstore.cart.controller;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.dto.CartItem;
import com.example.techstore.cart.service.CartService;
import com.example.techstore.common.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CartService cartService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void getCart_Success() throws Exception {
        CartDTO cart = new CartDTO();
        cart.setTotalPrice(BigDecimal.valueOf(100));

        when(cartService.getCart(any())).thenReturn(cart);

        mockMvc.perform(get("/api/v1/cart")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(100));
    }

    @Test
    void addToCart_Success() throws Exception {
        when(cartService.addToCart(any(), any(), any())).thenReturn(new CartDTO());

        mockMvc.perform(post("/api/v1/cart/add")
                        .header("Authorization", "Bearer token")
                        .param("productId", "10")
                        .param("quantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void clearCart_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/cart/clear")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}