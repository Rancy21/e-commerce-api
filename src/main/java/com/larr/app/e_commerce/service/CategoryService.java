package com.larr.app.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.Category;
import com.larr.app.e_commerce.repository.CategoryRepository;

@Service
public class CategoryService {
  private final CategoryRepository repository;

  public CategoryService(CategoryRepository repository) {
    this.repository = repository;
  }

  public Category findCategoryById(String id) {
    Optional<Category> existingCategory = repository.findById(id);
    if (existingCategory.isPresent()) {
      return existingCategory.get();
    } else {
      return null;
    }
  }

  public Category findCategoryByName(String name) {
    Optional<Category> existingCategory = repository.findCategoryByName(name);
    if (existingCategory.isPresent()) {
      return existingCategory.get();
    } else {
      return null;
    }
  }

  public Category updCategoryName(Category category, String name) {
    category.setName(name);
    return repository.save(category);
  }

  public List<Category> findAllCategories() {
    return repository.findAll();
  }

  public List<Category> findAllCategories(String name) {
    return repository.findByNameIgnoreCase(name);
  }
}
