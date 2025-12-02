package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.model.Favorite;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.FavoriteRepository;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void toggleFavorite_Add() {
        Long userId = 1L;
        Long productId = 10L;

        when(favoriteRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        favoriteService.toggleFavorite(userId, productId);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void toggleFavorite_Remove() {
        Long userId = 1L;
        Long productId = 10L;
        Favorite fav = new Favorite();

        when(favoriteRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);
        when(favoriteRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(fav));

        favoriteService.toggleFavorite(userId, productId);

        verify(favoriteRepository).delete(fav);
    }

    @Test
    void getUserFavorites_Success() {
        Long userId = 1L;
        Favorite fav = new Favorite();
        fav.setProduct(new Product());
        Page<Favorite> page = new PageImpl<>(Collections.singletonList(fav));

        when(favoriteRepository.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(ProductDTO.class))).thenReturn(new ProductDTO());

        Page<ProductDTO> result = favoriteService.getUserFavorites(userId, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}