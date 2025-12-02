package com.example.techstore.order.controller;

import com.example.techstore.common.util.JwtTokenUtils;
import com.example.techstore.order.dto.OrderDTO;
import com.example.techstore.order.model.OrderStatus;
import com.example.techstore.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private OrderService orderService;
    @MockBean private JwtTokenUtils jwtTokenUtils;

    @Test
    void createOrder_Success() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(123L);
        orderDTO.setStatus(OrderStatus.CREATED);

        when(orderService.createOrder(any(), any(), any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer token")
                        .param("deliveryAddress", "Minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123));
    }

    @Test
    void getUserOrders_Success() throws Exception {
        when(orderService.getUserOrders(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void payOrder_Success() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setStatus(OrderStatus.PAID);

        when(orderService.payOrder(eq(1L), any())).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/orders/1/pay")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}