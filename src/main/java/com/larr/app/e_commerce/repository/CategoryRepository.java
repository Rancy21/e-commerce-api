package com.larr.app.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAll();

    List<Category> findByNameIgnoreCase(String name);

    Optional<Category> findCategoryByName(String name);
}
