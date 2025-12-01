package com.example.techstore.catalog.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailsDTO extends ProductDTO {
    private String description;
    private List<String> images;
    private List<ProductAttributeDTO> attributes;
}