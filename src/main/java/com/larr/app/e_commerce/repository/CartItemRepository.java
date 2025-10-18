package com.larr.app.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

}
