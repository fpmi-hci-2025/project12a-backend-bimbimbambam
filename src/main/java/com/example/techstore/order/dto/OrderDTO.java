package com.example.techstore.order.dto;

import com.example.techstore.order.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private String contactPhone;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}
