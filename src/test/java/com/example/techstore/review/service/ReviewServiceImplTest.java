package com.example.techstore.review.service;

import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.order.model.Order;
import com.example.techstore.order.model.OrderItem;
import com.example.techstore.order.model.OrderStatus;
import com.example.techstore.order.repository.OrderRepository;
import com.example.techstore.review.dto.ReviewDTO;
import com.example.techstore.review.dto.ReviewResponseDTO;
import com.example.techstore.review.model.Review;
import com.example.techstore.review.repository.ReviewRepository;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private ReviewServiceImpl reviewService;

    @Test
    void addReview_Success() {
        Long userId = 1L;
        Long productId = 10L;
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(productId);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great!");

        User user = new User();
        user.setId(userId);
        Product product = new Product();
        product.setId(productId);

        // Mock User and Product
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

        // Mock Purchase History (Order)
        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);
        OrderItem item = new OrderItem();
        item.setProduct(product);
        order.setItems(List.of(item));
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        // Mock Save
        Review savedReview = new Review();
        savedReview.setUser(user);
        savedReview.setProduct(product);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(modelMapper.map(any(), eq(ReviewResponseDTO.class))).thenReturn(new ReviewResponseDTO());

        // Call
        ReviewResponseDTO result = reviewService.addReview(userId, reviewDTO);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
        verify(productRepository).save(product); // Verifies rating update
    }

    @Test
    void addReview_NotPurchased_ThrowsException() {
        Long userId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setProductId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Product product = new Product();
        product.setId(10L);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> reviewService.addReview(userId, reviewDTO));
    }

    @Test
    void getProductReviews_Success() {
        Long productId = 10L;
        Pageable pageable = Pageable.unpaged();
        Review review = new Review();
        User user = new User();
        user.setId(1L);
        review.setUser(user);
        Product product = new Product();
        product.setId(productId);
        review.setProduct(product);

        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(new PageImpl<>(List.of(review)));
        when(modelMapper.map(any(), eq(ReviewResponseDTO.class))).thenReturn(new ReviewResponseDTO());

        Page<ReviewResponseDTO> result = reviewService.getProductReviews(productId, pageable);

        assertFalse(result.isEmpty());
    }
}