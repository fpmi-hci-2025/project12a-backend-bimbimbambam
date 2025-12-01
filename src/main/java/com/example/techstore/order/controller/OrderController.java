package com.example.techstore.order.controller;

import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.order.dto.OrderDTO;
import com.example.techstore.order.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @RequestParam String deliveryAddress,
            @RequestParam(required = false) String contactPhone
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(orderService.createOrder(userId, deliveryAddress, contactPhone));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(orderService.getOrderById(id, userId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDTO> payOrder(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(orderService.payOrder(id, userId));
    }

    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenUtils.getUserId(jwt);
    }
}