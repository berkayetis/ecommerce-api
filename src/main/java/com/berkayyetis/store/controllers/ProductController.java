package com.berkayyetis.store.controllers;

import com.berkayyetis.store.dtos.ProductDto;
import com.berkayyetis.store.mapper.ProductMapper;
import com.berkayyetis.store.repositories.CategoryRepository;
import com.berkayyetis.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Iterable<ProductDto>> getAllProducts(
            @RequestParam(required = false) Byte categoryId) {

        if (categoryId != null) {
            var productDtos = productRepository.findProductsByCategoryId(categoryId)
                    .stream()
                    .map(productMapper::toDto)
                    .toList();

            if (productDtos.size() > 0) {
                return ResponseEntity.ok(productDtos);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        }

        else{
            var productDtos = productRepository.findProductsWithCategory()
                    .stream()
                    .map(productMapper::toDto)
                    .toList();

            if (productDtos.size() > 0) {
                return ResponseEntity.ok(productDtos);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        var productDto = productRepository.findById(id).orElse(null);
        if (productDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(productDto));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        System.out.println(product);
        productRepository.save(product);

        productDto.setId(product.getId());

        var uri = uriBuilder.path("/products.json/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(uri).body(productMapper.toDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ProductDto productDto)
    {
        var product = productRepository.findById(id).orElse(null);
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

        if (product == null || category == null) {
            return ResponseEntity.notFound().build();
        }
        productMapper.update(productDto, product);
        product.setCategory(category);

        productRepository.save(product);

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        var product=productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
