package com.example.techstore.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO {
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private Integer totalQuantity = 0;

    public void calculateTotals() {
        this.totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        this.totalPrice = items.stream()
                .map(item -> item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}