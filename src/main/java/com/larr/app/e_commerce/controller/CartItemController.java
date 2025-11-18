package com.larr.app.e_commerce.controller;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.CartItemUpdateRequest;
import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.CartItem;
import com.larr.app.e_commerce.security.service.CartService;
import com.larr.app.e_commerce.service.CartItemService;

@RestController
@RequestMapping(value = "/api/carts/{cartId}/items")
public class CartItemController {
  private final CartItemService service;
  private final CartService cartService;

  public CartItemController(CartItemService cartItemService, CartService cartService) {
    this.service = cartItemService;
    this.cartService = cartService;
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<?> getCart(@PathVariable String itemId, @PathVariable String cartId) {
    return findCartItemAndProceed(itemId, cartId, ResponseEntity::ok);
  }

  @PostMapping("/save")
  public ResponseEntity<?> addItemtoCart(@PathVariable String cartId, @RequestBody CartItem item) {
    Cart cart = cartService.findCart(cartId);
    return ResponseEntity.ok(service.addToCart(cart, item));
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<?> updateCartItem(@PathVariable String cartId, @PathVariable String itemId,
      @RequestBody CartItemUpdateRequest request) {
    return findCartItemAndProceed(itemId, cartId,
        item -> ResponseEntity.ok(service.updateCartItemQuantity(item, request.getQuantity())));

  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<?> removeItemFromCart(@PathVariable String cartId, @PathVariable String itemId) {
    return findCartItemAndProceed(itemId, cartId, item -> {
      service.removeItemFromCart(item);
      return ResponseEntity.ok("Deleted successfully");
    });
  }

  @DeleteMapping
  public ResponseEntity<?> removeAllItemsFromCart(@PathVariable String cartId) {
    Cart cart = cartService.findCart(cartId);
    service.removeAllItemFromCart(cart);
    return ResponseEntity.ok("Deleted successfully");
  }

  @GetMapping
  public ResponseEntity<?> listAllCartItems(@PathVariable String cartId) {
    Cart cart = cartService.findCart(cartId);
    return ResponseEntity.ok(service.listAllCartItems(cart));
  }

  private ResponseEntity<?> findCartItemAndProceed(String itemId, String cartId,
      Function<CartItem, ResponseEntity<?>> action) {
    CartItem item = service.findCartItem(itemId, cartId);

    if (item != null) {
      return action.apply(item);
    }

    return new ResponseEntity<>("Cart item with id:" + itemId + " not found", HttpStatus.NOT_FOUND);
  }
}