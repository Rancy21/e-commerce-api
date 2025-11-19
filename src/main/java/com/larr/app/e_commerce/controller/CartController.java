package com.larr.app.e_commerce.controller;

import java.util.function.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.CartUpdateRequest;
import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.security.service.CartService;
import com.larr.app.e_commerce.service.UserService;

@RestController
@RequestMapping("/api/carts")
public class CartController {

  private final CartService service;
  private final UserService userService;

  public CartController(CartService service, UserService userService) {
    this.service = service;
    this.userService = userService;
  }

  @PostMapping("/save")
  public ResponseEntity<?> createCart(@RequestBody Cart cart) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetails user = (UserDetails) authentication.getPrincipal();

    cart.setUser(userService.getUser(user.getUsername()).get());

    return ResponseEntity.ok(service.createCart(cart));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCart(@PathVariable String id) {
    return findCartAndProceed(id, ResponseEntity::ok);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<?> updateCartStatus(@PathVariable String id, @RequestBody CartUpdateRequest request) {
    return findCartAndProceed(id, cart -> ResponseEntity.ok(service.updateCartStatus(request.getStatus(), cart)));
  }

  @PatchMapping("/{id}/price")
  public ResponseEntity<?> updateCartPrice(@PathVariable String id, @RequestBody CartUpdateRequest request) {
    return findCartAndProceed(id, cart -> ResponseEntity.ok(service.updateCartPrice(cart, request.getTotalprice())));
  }

  private ResponseEntity<?> findCartAndProceed(String id, Function<Cart, ResponseEntity<?>> action) {
    Cart cart = service.findCart(id);

    if (cart != null) {
      return action.apply(cart);
    }
    return new ResponseEntity<>("Cart with ID: " + id + " not found.", HttpStatus.NOT_FOUND);

  }

}