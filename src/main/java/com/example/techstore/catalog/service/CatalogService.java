package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.BrandDTO;
import com.example.techstore.catalog.dto.CategoryDTO;
import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CatalogService {
    List<CategoryDTO> getAllCategories();
    List<BrandDTO> getAllBrands();

    Page<ProductDTO> searchProducts(
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<Integer> brandIds,
            Integer categoryId,
            Boolean inStock,
            BigDecimal minRating,
            Pageable pageable
    );

    ProductDetailsDTO getProductById(Long id);
}