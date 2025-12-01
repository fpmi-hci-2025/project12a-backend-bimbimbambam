package com.example.techstore.order.service;

import com.example.techstore.order.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId, String deliveryAddress, String contactPhone);
    OrderDTO payOrder(Long orderId, Long userId);
    List<OrderDTO> getUserOrders(Long userId);
    OrderDTO getOrderById(Long orderId, Long userId);
}
