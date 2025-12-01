package com.example.techstore.catalog.repository;

import com.example.techstore.catalog.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    Page<Favorite> findAllByUserId(Long userId, Pageable pageable);
}