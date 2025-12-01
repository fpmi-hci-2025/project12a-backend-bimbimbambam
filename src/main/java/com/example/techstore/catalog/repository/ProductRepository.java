package com.example.techstore.catalog.repository;

import com.example.techstore.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"brand", "category", "images"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"brand", "category", "images", "attributes"})
    Optional<Product> findById(Long id);
}