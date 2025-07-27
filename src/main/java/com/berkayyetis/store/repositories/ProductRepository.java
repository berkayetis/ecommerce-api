package com.berkayyetis.store.repositories;

import com.berkayyetis.store.dtos.ProductDto;
import com.berkayyetis.store.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "category")
    List<Product> findProductsByCategoryId(Byte categoryId);

    @EntityGraph(attributePaths = "category")
    @Query("select product from Product product")
    List<Product> findProductsWithCategory();
}