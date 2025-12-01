package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.model.Brand;
import com.example.techstore.catalog.model.Category;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.catalog.util.CatalogException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    @Test
    void getProductById_Success() {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setImages(new ArrayList<>());
        product.setAttributes(new ArrayList<>());
        product.setBrand(new Brand());
        product.setCategory(new Category());

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDetailsDTO.class)).thenReturn(new ProductDetailsDTO());

        ProductDetailsDTO result = catalogService.getProductById(id);

        assertNotNull(result);
    }

    @Test
    void getProductById_NotFound() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CatalogException.class, () -> catalogService.getProductById(id));
    }
}