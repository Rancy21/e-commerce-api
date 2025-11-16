package com.larr.app.e_commerce.controller;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.ProductUpdateRequest;
import com.larr.app.e_commerce.model.Category;
import com.larr.app.e_commerce.model.Product;
import com.larr.app.e_commerce.service.CategoryService;
import com.larr.app.e_commerce.service.ProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
  @Autowired
  ProductService service;

  @Autowired
  CategoryService categoryService;

  @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> saveProduct(@RequestBody Product product) {
    Product existingProduct = service.findProductByName(product.getName());
    if (existingProduct != null) {
      return new ResponseEntity<>("Product with name: " + product.getName() + " already exists",
          HttpStatus.BAD_REQUEST);

    } else {
      return ResponseEntity.ok(service.createProduct(product));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> findProduct(@PathVariable String id) {
    return findProductByIdAndProceed(id, ResponseEntity::ok);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping(value = "/{id}/increase")
  public ResponseEntity<?> increaseProductQuantity(@PathVariable String id, @RequestBody ProductUpdateRequest request) {
    return findProductByIdAndProceed(id, product -> {
      if (request.getQuantity() == null || request.getQuantity() <= 0) {
        return new ResponseEntity<>("Quantity to increase must be positive", HttpStatus.BAD_REQUEST);
      }
      return ResponseEntity.ok(service.increaseProductQuantity(product, request.getQuantity()));
    });
  }

  @PatchMapping("/{id}/decrease")
  public ResponseEntity<?> decreaseProductQuaEntity(@PathVariable String id,
      @RequestBody ProductUpdateRequest request) {
    return findProductByIdAndProceed(id, product -> {
      if (request.getQuantity() == null || request.getQuantity() <= 0) {
        return new ResponseEntity<>("Quantity to decrease must be positive", HttpStatus.BAD_REQUEST);
      }
      if (product.getQuantity() >= request.getQuantity()) {
        return ResponseEntity.ok(service.decreaseProductQuantity(product, request.getQuantity()));
      } else {
        return new ResponseEntity<>("Cannot decrease. Not enough in stock", HttpStatus.CONFLICT);
      }
    });
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/name")
  public ResponseEntity<?> updateProductName(@PathVariable String id,
      @RequestBody ProductUpdateRequest request) {
    return findProductByIdAndProceed(id, product -> {
      if (request.getName() == null || request.getName().isBlank()) {
        return new ResponseEntity<>("Product name cannot be empty.", HttpStatus.BAD_REQUEST);
      }
      return ResponseEntity.ok(service.updateProductName(product, request.getName()));
    });
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping(value = "/{id}/price")
  public ResponseEntity<?> updateProductPrice(@PathVariable String id,
      @RequestBody ProductUpdateRequest request) {
    return findProductByIdAndProceed(id, product -> {
      if (request.getPrice() == null || request.getPrice() < 0) {
        return new ResponseEntity<>("Price cannot be negative.", HttpStatus.BAD_REQUEST);
      }
      return ResponseEntity.ok(service.updateProductPrice(product, request.getPrice()));
    });
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/image")
  public ResponseEntity<?> updateProductImageUrl(@PathVariable String id,
      @RequestBody ProductUpdateRequest request) {
    return findProductByIdAndProceed(id,
        product -> ResponseEntity.ok(service.updateProductImageUrl(product, request.getImageUrl())));
  }

  @GetMapping("/by-category")
  public ResponseEntity<?> findProductsByCategory(@RequestParam String category) {
    Category productCategory = categoryService.findCategoryByName(category);
    if (productCategory != null) {
      List<Product> products = service.findProductsByCategory(productCategory);
      if (products != null && !products.isEmpty()) {
        return ResponseEntity.ok(products);
      } else {
        return new ResponseEntity<>("No products found for the given category", HttpStatus.NOT_FOUND);
      }
    } else {
      return new ResponseEntity<>("Category " + category + " not found", HttpStatus.NOT_FOUND);
    }

  }

  @GetMapping("/by-name")
  public ResponseEntity<?> findProductsByName(@RequestParam String name) {
    List<Product> products = service.findProductsByName(name);
    if (products == null || products.isEmpty()) {
      return new ResponseEntity<>("No product match", HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(products);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> findProducts() {
    List<Product> products = service.findAllProducts();
    if (products == null || products.isEmpty()) {
      return new ResponseEntity<>("No product match", HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(products);
    }
  }

  @DeleteMapping(value = "/{id}/delete")
  private ResponseEntity<?> deleteProduct(@PathVariable String id) {
    return findProductByIdAndProceed(id, product -> ResponseEntity.ok(service.deleteProduct(product)));
  }

  private ResponseEntity<?> findProductByIdAndProceed(String id, Function<Product, ResponseEntity<?>> action) {
    Product product = service.findProductById(id);
    if (product != null) {
      return action.apply(product);
    } else {
      return new ResponseEntity<>("Product with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

}
