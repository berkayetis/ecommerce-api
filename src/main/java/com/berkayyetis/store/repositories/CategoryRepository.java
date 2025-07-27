package com.berkayyetis.store.repositories;

import com.berkayyetis.store.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}