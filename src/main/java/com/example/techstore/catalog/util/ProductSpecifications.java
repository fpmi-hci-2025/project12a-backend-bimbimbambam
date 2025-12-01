package com.example.techstore.catalog.util;

import com.example.techstore.catalog.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> withFilter(
            String searchQuery,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<Integer> brandIds,
            Integer categoryId,
            Boolean inStock,
            BigDecimal minRating
    ) {
        return Specification.where(hasTitleOrDescription(searchQuery))
                .and(priceBetween(minPrice, maxPrice))
                .and(belongsToBrands(brandIds))
                .and(belongsToCategory(categoryId))
                .and(isAvailable(inStock))
                .and(hasMinRating(minRating));
    }

    private static Specification<Product> hasTitleOrDescription(String query) {
        return (root, query1, cb) -> {
            if (query == null || query.isEmpty()) return null;
            String likePattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    private static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    private static Specification<Product> belongsToBrands(List<Integer> brandIds) {
        return (root, query, cb) -> {
            if (brandIds == null || brandIds.isEmpty()) return null;
            return root.get("brand").get("id").in(brandIds);
        };
    }

    private static Specification<Product> belongsToCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    private static Specification<Product> isAvailable(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null || !inStock) return null;
            return cb.greaterThan(root.get("quantity"), 0);
        };
    }

    private static Specification<Product> hasMinRating(BigDecimal minRating) {
        return (root, query, cb) -> {
            if (minRating == null) return null;
            return cb.greaterThanOrEqualTo(root.get("averageRating"), minRating);
        };
    }
}