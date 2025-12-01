package com.example.techstore.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productTitle;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private String imageUrl;
}