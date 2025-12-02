package com.example.techstore.cart.service;

import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComparisonServiceImplTest {

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOperations;
    @Mock private CatalogService catalogService;

    @InjectMocks private ComparisonServiceImpl comparisonService;

    private final Long USER_ID = 1L;
    private final String COMPARE_KEY = "compare:" + USER_ID;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getComparison_Success() {
        Set<Long> ids = new HashSet<>();
        ids.add(10L);
        when(valueOperations.get(COMPARE_KEY)).thenReturn(ids);
        when(catalogService.getProductById(10L)).thenReturn(new ProductDetailsDTO());

        List<ProductDetailsDTO> result = comparisonService.getComparison(USER_ID);

        assertEquals(1, result.size());
    }

    @Test
    void toggleProduct_AddNew() {
        when(valueOperations.get(COMPARE_KEY)).thenReturn(new HashSet<>());
        when(catalogService.getProductById(10L)).thenReturn(new ProductDetailsDTO());

        comparisonService.toggleProduct(USER_ID, 10L);

        verify(valueOperations).set(eq(COMPARE_KEY), any(), any());
    }

    @Test
    void toggleProduct_RemoveExisting() {
        Set<Long> ids = new HashSet<>();
        ids.add(10L);
        when(valueOperations.get(COMPARE_KEY)).thenReturn(ids);

        comparisonService.toggleProduct(USER_ID, 10L);

        verify(valueOperations).set(eq(COMPARE_KEY), argThat(set -> ((Set)set).isEmpty()), any());
    }

    @Test
    void clearComparison() {
        comparisonService.clearComparison(USER_ID);
        verify(redisTemplate).delete(COMPARE_KEY);
    }
}