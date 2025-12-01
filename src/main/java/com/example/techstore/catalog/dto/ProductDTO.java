package com.example.techstore.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal averageRating;
    private String imageUrl;
    private String brandName;
    private String categoryName;
    private Integer quantity;
}