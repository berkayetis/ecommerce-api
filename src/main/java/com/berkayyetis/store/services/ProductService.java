package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.ProductDto;
import com.berkayyetis.store.mapper.ProductMapper;
import com.berkayyetis.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDto getProductById(long id) {
        var product = productRepository.findById(id).orElse(null);
        return productMapper.toDto(product);
    }
}
