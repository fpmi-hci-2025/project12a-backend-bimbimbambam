package com.example.techstore.review.repository;

import com.example.techstore.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRating(Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}