package com.example.techstore.order.repository;

import com.example.techstore.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "items", "items.product"})
    Page<Order> findAll(Pageable pageable);
}