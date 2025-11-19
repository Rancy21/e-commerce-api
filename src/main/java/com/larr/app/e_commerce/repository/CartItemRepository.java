package com.larr.app.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
  List<CartItem> findByCart(Cart cart);

  Optional<CartItem> findByIdAndCartId(String id, String cartId);

  @Modifying
  @Query("delete from CartItem c where c.cart.id = :id")
  void deleteByCart(@Param("id") String cartId);
}
