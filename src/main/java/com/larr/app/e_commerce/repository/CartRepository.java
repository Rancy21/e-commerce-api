package com.larr.app.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findAll();
}
