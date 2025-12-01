package com.example.techstore.catalog.controller;

import com.example.techstore.catalog.dto.BrandDTO;
import com.example.techstore.catalog.dto.CategoryDTO;
import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        return ResponseEntity.ok(catalogService.getAllBrands());
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<Integer> brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BigDecimal minRating,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(catalogService.searchProducts(
                query, minPrice, maxPrice, brandId, categoryId, inStock, minRating, pageable
        ));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDetailsDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }
}