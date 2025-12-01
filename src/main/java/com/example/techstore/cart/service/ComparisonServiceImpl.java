package com.example.techstore.cart.service;

import com.example.techstore.cart.util.CartException;
import com.example.techstore.catalog.dto.ProductDetailsDTO;
import com.example.techstore.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonServiceImpl implements ComparisonService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CatalogService catalogService;

    private static final String COMPARE_PREFIX = "compare:";
    private static final Duration COMPARE_TTL = Duration.ofDays(1);
    private static final int MAX_ITEMS = 4;

    @Override
    public List<ProductDetailsDTO> getComparison(Long userId) {
        Set<Long> productIds = getProductIds(userId);
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        return productIds.stream()
                .map(catalogService::getProductById)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDetailsDTO> toggleProduct(Long userId, Long productId) {
        String key = getCompareKey(userId);
        Set<Long> productIds = getProductIds(userId);

        if (productIds == null) {
            productIds = new HashSet<>();
        }

        if (productIds.contains(productId)) {
            productIds.remove(productId);
        } else {
            if (productIds.size() >= MAX_ITEMS) {
                throw new CartException("Maximum " + MAX_ITEMS + " products allowed for comparison");
            }
            catalogService.getProductById(productId);
            productIds.add(productId);
        }

        redisTemplate.opsForValue().set(key, productIds, COMPARE_TTL);
        return getComparison(userId);
    }

    @Override
    public void clearComparison(Long userId) {
        redisTemplate.delete(getCompareKey(userId));
    }

    private Set<Long> getProductIds(Long userId) {
        String key = getCompareKey(userId);
        return (Set<Long>) redisTemplate.opsForValue().get(key);
    }

    private String getCompareKey(Long userId) {
        return COMPARE_PREFIX + userId;
    }
}