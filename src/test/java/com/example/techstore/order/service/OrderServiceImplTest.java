package com.example.techstore.order.service;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.dto.CartItem;
import com.example.techstore.cart.service.CartServiceImpl;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.order.dto.OrderDTO;
import com.example.techstore.order.event.OrderEventProducer;
import com.example.techstore.order.model.Order;
import com.example.techstore.order.repository.OrderRepository;
import com.example.techstore.order.util.OrderException;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartServiceImpl cartService;
    @Mock private OrderEventProducer orderEventProducer;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_Successful() {
        Long userId = 1L;
        String address = "Minsk";

        User user = new User();
        user.setId(userId);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CartDTO cart = new CartDTO();
        CartItem item = new CartItem(100L, "Phone", 1, BigDecimal.valueOf(1000), "url");
        cart.setItems(List.of(item));
        cart.setTotalPrice(BigDecimal.valueOf(1000));
        when(cartService.getCart(userId)).thenReturn(cart);

        Product product = new Product();
        product.setId(100L);
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(1000));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        Order savedOrder = new Order();
        savedOrder.setId(555L);
        savedOrder.setTotalPrice(BigDecimal.valueOf(1000));
        savedOrder.setItems(List.of());
        savedOrder.setUser(user);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(new OrderDTO());

        orderService.createOrder(userId, address, null);

        verify(productRepository).save(product);
        assertEquals(9, product.getQuantity());
        verify(cartService).clearCart(userId);
        verify(orderEventProducer).sendOrderPlacedEvent(any());
    }

    @Test
    void createOrder_ThrowsException_WhenCartEmpty() {
        Long userId = 1L;
        CartDTO emptyCart = new CartDTO();
        when(cartService.getCart(userId)).thenReturn(emptyCart);

        assertThrows(OrderException.class, () ->
                orderService.createOrder(userId, "Address", null)
        );

        verify(orderRepository, never()).save(any());
    }
}