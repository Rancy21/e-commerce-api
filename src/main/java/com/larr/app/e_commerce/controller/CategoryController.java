package com.larr.app.e_commerce.controller;

import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.CategoryUpdateRequest;
import com.larr.app.e_commerce.model.Category;
import com.larr.app.e_commerce.service.CategoryService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findCategoryById(@PathVariable String id) {
    Category category = service.findCategoryById(id);

    if (category != null) {
      return ResponseEntity.ok(category);
    }
    return new ResponseEntity<>("Category with: " + id + " does not exist", HttpStatus.NOT_FOUND);
  }

  @PatchMapping("/{id}/name")
  public ResponseEntity<?> updateCategoryName(@PathVariable String id, @RequestBody CategoryUpdateRequest request) {
    Category category = service.findCategoryById(id);

    if (category != null) {
      if (request.getName() == null || request.getName().isBlank()) {
        return new ResponseEntity<>("Category name cannot be empty", HttpStatus.BAD_REQUEST);
      }

      return ResponseEntity.ok(service.updCategoryName(category, request.getName()));
    }

    return new ResponseEntity<>("Category with: " + id + " does not exist", HttpStatus.NOT_FOUND);
  }

  @GetMapping(value = "/{id}/all")
  public ResponseEntity<?> findAllCategories() {
    List<Category> categories = service.findAllCategories();

    if (categories == null || categories.isEmpty()) {
      return new ResponseEntity<>("No categories in the database", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(categories);
  }

  @GetMapping(value = "/{id}/all-by-name")
  public ResponseEntity<?> findAllCategoriesByName(@RequestParam String name) {
    List<Category> categories = service.findAllCategories(name);

    if (categories == null || categories.isEmpty()) {
      return new ResponseEntity<>("No categories match", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(categories);
  }

}
