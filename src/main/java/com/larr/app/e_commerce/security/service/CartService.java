package com.larr.app.e_commerce.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.CartStatus;
import com.larr.app.e_commerce.repository.CartRepository;

@Service
public class CartService {
  private final CartRepository repository;

  public CartService(CartRepository repository) {
    this.repository = repository;
  }

  public Cart createCart(Cart cart) {
    return repository.save(cart);
  }

  public Cart findCart(String id) {
    Optional<Cart> cart = repository.findById(id);
    if (cart.isPresent()) {
      return cart.get();
    } else {
      return null;
    }
  }

  public Cart findCart(String userId, CartStatus status) {
    Optional<Cart> cart = repository.findCartByUserIdAndStatus(userId, status);
    if (cart.isPresent()) {
      return cart.get();
    } else {
      return null;
    }
  }

  public Cart updateCartStatus(CartStatus status, Cart cart) {
    cart.setStatus(status);
    return repository.save(cart);
  }

  public Cart updateCartPrice(Cart cart, double price) {
    cart.setTotalprice(price);
    return repository.save(cart);
  }

  public List<Cart> findAllCarts() {
    return repository.findAll();
  }

  public List<Cart> findAllByUser(String userId) {
    return repository.findByUserId(userId);
  }
}
