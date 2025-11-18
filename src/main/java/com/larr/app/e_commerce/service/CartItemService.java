package com.larr.app.e_commerce.service;

import java.util.List;
import java.util.Optional;

import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.CartItem;
import com.larr.app.e_commerce.repository.CartItemRepository;

public class CartItemService {

  private final CartItemRepository repository;

  public CartItemService(CartItemRepository repository) {
    this.repository = repository;
  }

  public CartItem addToCart(Cart cart, CartItem item) {
    item.setCart(cart);
    return repository.save(item);
  }

  public CartItem updCartItemQuantity(CartItem item, int quantity) {
    item.setQuantity(quantity);
    item.setSubtotal(quantity * item.getPrice());

    return repository.save(item);
  }

  public CartItem findCartItem(String id, String cartId) {
    Optional<CartItem> item = repository.findByIdAndCartId(id, cartId);

    if (item.isPresent()) {
      return item.get();
    }
    return null;
  }

  public void removeItemFromCart(CartItem item) {
    repository.delete(item);
  }

  public List<CartItem> listAllCartItems(Cart cart) {
    return repository.findByCart(cart);
  }

  public void removeAllItemFromCart(Cart cart) {
    repository.deleteAllByCart(cart);
  }

}