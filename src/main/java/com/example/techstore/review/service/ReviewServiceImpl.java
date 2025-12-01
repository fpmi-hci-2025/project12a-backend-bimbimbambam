package com.example.techstore.review.service;

import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.order.model.OrderStatus;
import com.example.techstore.order.repository.OrderRepository;
import com.example.techstore.review.dto.ReviewDTO;
import com.example.techstore.review.dto.ReviewResponseDTO;
import com.example.techstore.review.model.Review;
import com.example.techstore.review.repository.ReviewRepository;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ReviewResponseDTO addReview(Long userId, ReviewDTO reviewDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean hasPurchased = orderRepository.findByUserId(userId).stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.PAID)
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (!hasPurchased) {
            throw new RuntimeException("Вы можете оставить отзыв только на купленный товар");
        }

        if (reviewRepository.existsByUserIdAndProductId(userId, product.getId())) {
            throw new RuntimeException("Вы уже оставили отзыв на этот товар");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);

        updateProductRating(product);

        return convertToDTO(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);

        updateProductRating(product);
    }

    private void updateProductRating(Product product) {
        Double average = reviewRepository.getAverageRating(product.getId());
        BigDecimal newRating = BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP); // Округляем до 1 знака (4.5)

        product.setAverageRating(newRating);
        productRepository.save(product);
    }

    private ReviewResponseDTO convertToDTO(Review review) {
        ReviewResponseDTO dto = modelMapper.map(review, ReviewResponseDTO.class);
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setProductId(review.getProduct().getId());
        return dto;
    }
}