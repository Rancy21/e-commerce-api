package com.larr.app.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.CartStatus;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findAll();

    Optional<Cart> findCartByUserIdAndStatus(String userId, CartStatus status);
}
