package com.example.techstore.catalog.service;

import com.example.techstore.catalog.dto.ProductDTO;
import com.example.techstore.catalog.model.Favorite;
import com.example.techstore.catalog.model.Product;
import com.example.techstore.catalog.repository.FavoriteRepository;
import com.example.techstore.catalog.repository.ProductRepository;
import com.example.techstore.catalog.util.CatalogException;
import com.example.techstore.user.model.User;
import com.example.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                    .orElseThrow(() -> new CatalogException("Favorite not found"));
            favoriteRepository.delete(favorite);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CatalogException("User not found"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CatalogException("Product not found"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
        }
    }

    @Override
    public Page<ProductDTO> getUserFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findAllByUserId(userId, pageable)
                .map(favorite -> {
                    Product product = favorite.getProduct();
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setBrandName(product.getBrand() != null ? product.getBrand().getName() : null);
                    dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
                    if (!product.getImages().isEmpty()) {
                        dto.setImageUrl(product.getImages().get(0).getUrl());
                    }
                    return dto;
                });
    }
}