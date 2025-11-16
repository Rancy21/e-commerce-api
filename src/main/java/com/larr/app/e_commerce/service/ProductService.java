package com.larr.app.e_commerce.service;

import java.util.List;
import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.Category;
import com.larr.app.e_commerce.model.Product;
import com.larr.app.e_commerce.repository.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository repo;

  public ProductService(ProductRepository repo) {
    this.repo = repo;
  }

  public Product createProduct(Product product) {
    return repo.save(product);
  }

  public Product findProductById(String id) {
    Optional<Product> existingProduct = repo.findById(id);
    if (existingProduct.isPresent()) {
      return existingProduct.get();
    } else {
      return null;
    }
  }

  public Product findProductByName(String name) {
    Optional<Product> existingProduct = repo.findProductByName(name);
    if (existingProduct.isPresent()) {
      return existingProduct.get();
    } else {
      return null;
    }
  }

  public Product increaseProductQuantity(Product product, int quantity) {
    int newQuantity = product.getQuantity() + quantity;

    product.setQuantity(newQuantity);

    return repo.save(product);
  }

  public Product decreaseProductQuantity(Product product, int quantity) {
    int newQuantity = product.getQuantity() - quantity;

    product.setQuantity(newQuantity);

    return repo.save(product);
  }

  public Product updateProductName(Product product, String name) {
    product.setName(name);
    return repo.save(product);
  }

  public Product updateProductCategory(Product product, Category category) {
    product.setCategory(category);
    return repo.save(product);
  }

  public Product deleteProduct(Product product) {
    product.setActive(false);
    return repo.save(product);
  }

  public Product updateProductDescription(Product product, String description) {
    product.setDescription(description);
    return repo.save(product);
  }

  public Product updateProductPrice(Product product, double price) {
    product.setPrice(price);
    return repo.save(product);
  }

  public Product updateProductImageUrl(Product product, String url) {
    product.setImgUrl(url);
    return repo.save(product);
  }

  public List<Product> findProductsByCategory(Category category) {
    return repo.findAllByCategory(category);
  }

  public List<Product> findProductsByName(String name) {
    return repo.findByNameContainingIgnoreCase(name);
  }

  public List<Product> findAllProducts() {
    return repo.findAll();
  }
}
