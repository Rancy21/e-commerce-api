package com.larr.app.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larr.app.e_commerce.model.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findAll();
}
