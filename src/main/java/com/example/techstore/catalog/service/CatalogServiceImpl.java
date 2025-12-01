package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.*;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.model.ProductImage;
import com.example.techstore.catalog.repository.BrandRepository;
import com.example.techstore.catalog.repository.CategoryRepository;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.catalog.util.CatalogException;
import com.example.techstore.catalog.util.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice,
                                           List<Integer> brandIds, Integer categoryId, Boolean inStock,
                                           BigDecimal minRating, Pageable pageable) {

        Specification<Product> spec = ProductSpecifications.withFilter(
                query, minPrice, maxPrice, brandIds, categoryId, inStock, minRating
        );

        return productRepository.findAll(spec, pageable)
                .map(this::convertToProductDTO);
    }

    @Override
    public ProductDetailsDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Product not found with id: " + id));
        return convertToProductDetailsDTO(product);
    }

    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        dto.setBrandName(product.getBrand() != null ? product.getBrand().getName() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);

        String mainImage = product.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl());

        dto.setImageUrl(mainImage);
        return dto;
    }

    private ProductDetailsDTO convertToProductDetailsDTO(Product product) {
        ProductDetailsDTO dto = modelMapper.map(product, ProductDetailsDTO.class);

        dto.setBrandName(product.getBrand() != null ? product.getBrand().getName() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);

        dto.setImages(product.getImages().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList()));

        String mainImage = product.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(dto.getImages().isEmpty() ? null : dto.getImages().get(0));
        dto.setImageUrl(mainImage);

        List<ProductAttributeDTO> attributes = product.getAttributes().stream()
                .map(attr -> {
                    ProductAttributeDTO attrDto = new ProductAttributeDTO();
                    attrDto.setName(attr.getName());
                    attrDto.setValue(attr.getValue());
                    return attrDto;
                })
                .collect(Collectors.toList());
        dto.setAttributes(attributes);

        return dto;
    }
}