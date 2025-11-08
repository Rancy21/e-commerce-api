package com.larr.app.e_commerce.repository;

import java.util.List;
import java.util.Locale.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("From Product p where p.isActive = true")
    List<Product> findAll();

    List<Product> findAllByCategory(Category category);

    List<Product> findByNameContainingIgnoreCase(String name);
}
