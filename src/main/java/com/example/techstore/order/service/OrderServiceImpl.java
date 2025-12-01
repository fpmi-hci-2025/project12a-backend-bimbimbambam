package com.example.techstore.order.service;

import com.example.techstore.cart.dto.CartDTO;
import com.example.techstore.cart.dto.CartItem;
import com.example.techstore.cart.service.CartServiceImpl;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.order.dto.OrderDTO;
import com.example.techstore.order.dto.OrderItemDTO;
import com.example.techstore.order.event.OrderEventProducer;
import com.example.techstore.order.event.OrderPlacedEvent;
import com.example.techstore.order.model.Order;
import com.example.techstore.order.model.OrderItem;
import com.example.techstore.order.model.OrderStatus;
import com.example.techstore.order.repository.OrderRepository;
import com.example.techstore.order.util.OrderException;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartServiceImpl cartServiceImpl;
    private final OrderEventProducer orderEventProducer;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, String deliveryAddress, String contactPhone) {
        CartDTO cart = cartServiceImpl.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new OrderException("Cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OrderException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setDeliveryAddress(deliveryAddress);
        order.setContactPhone(contactPhone != null ? contactPhone : user.getPhone());
        order.setTotalPrice(cart.getTotalPrice());

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new OrderException("Product not found: " + cartItem.getProductId()));

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new OrderException("Not enough stock for product: " + product.getTitle());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            product.setPopularity(product.getPopularity() + cartItem.getQuantity());

            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPricePerUnit());

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        cartServiceImpl.clearCart(userId);

        OrderPlacedEvent event = OrderPlacedEvent.newBuilder()
                .setOrderId(savedOrder.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setTotalPrice(savedOrder.getTotalPrice().toString())
                .setItemsCount(savedOrder.getItems().size())
                .build();

        try {
            orderEventProducer.sendOrderPlacedEvent(event);
        } catch (Exception e) {
            System.err.println("Failed to send Kafka event: " + e.getMessage());
        }

        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO payOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("Access denied");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderException("Order cannot be paid in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("Access denied");
        }

        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        dto.setUserId(order.getUser().getId());

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductTitle(item.getProduct().getTitle());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPriceAtPurchase(item.getPriceAtPurchase());
                    return itemDTO;
                }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}