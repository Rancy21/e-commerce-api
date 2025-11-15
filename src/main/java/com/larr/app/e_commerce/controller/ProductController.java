package com.larr.app.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.model.Category;
import com.larr.app.e_commerce.model.Product;
import com.larr.app.e_commerce.service.CategoryService;
import com.larr.app.e_commerce.service.ProductService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/product")
public class ProductController {
  @Autowired
  ProductService service;

  @Autowired
  CategoryService categoryService;

  @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> saveProduct(@RequestBody Product product) {
    Product existingProduct = service.findProductByName(product.getName());
    System.out.println(existingProduct);
    if (existingProduct != null) {
      return new ResponseEntity<>("Product with name: " + product.getName() + " already exists",
          HttpStatus.BAD_REQUEST);

    } else {
      System.out.println("");
      return ResponseEntity.ok(service.createProduct(product));
    }
  }

  @GetMapping("/find")
  public ResponseEntity<?> findProduct(@RequestParam String id) {
    Product product = service.findProductById(id);
    if (product != null) {
      return ResponseEntity.ok(product);
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/increase")
  public ResponseEntity<?> increaseProductQuantity(@RequestParam String id, @PathVariable int quantity) {
    Product product = service.findProductById(id);
    if (product != null) {
      return ResponseEntity.ok(service.increaseProductQuantity(product, quantity));
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/decrease")
  public ResponseEntity<?> decreaseProductQuaEntity(@RequestParam String id, @RequestParam int quantity) {
    Product product = service.findProductById(id);
    if (product != null) {
      if (product.getQuantity() > quantity) {
        return ResponseEntity.ok(service.increaseProductQuantity(product, quantity));
      } else {
        return new ResponseEntity<>("Cannot decease. not enough in stock", HttpStatus.CONFLICT);
      }
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/updateName")
  public ResponseEntity<?> updateProductName(@RequestParam String id, @RequestParam String name) {
    Product product = service.findProductById(id);
    if (product != null) {
      return ResponseEntity.ok(service.updateProductName(product, name));
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/updatePrice")
  public ResponseEntity<?> updateProductPrice(@RequestParam String id, @RequestParam double price) {
    Product product = service.findProductById(id);
    if (product != null) {
      return ResponseEntity.ok(service.updateProductPrice(product, price));
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/updateImage")
  public ResponseEntity<?> updateProductImageUrl(@RequestParam String id, @RequestParam String url) {
    Product product = service.findProductById(id);
    if (product != null) {
      return ResponseEntity.ok(service.updateProductImageUrl(product, url));
    } else {
      return new ResponseEntity<>("Prouduct with ID: " + id + " not found", HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/findByCategory")
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

  @GetMapping("/findByName")
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

}
