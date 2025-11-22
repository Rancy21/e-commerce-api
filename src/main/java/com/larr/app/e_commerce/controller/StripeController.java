package com.larr.app.e_commerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.CreatePaymentRequest;
import com.larr.app.e_commerce.dto.PaymentResponse;
import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.security.service.CartService;
import com.larr.app.e_commerce.service.StripeService;
import com.larr.app.e_commerce.service.UserService;
import com.stripe.model.PaymentIntent;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment/stripe")
@RequiredArgsConstructor
public class StripeController {
  private final StripeService service;
  private final CartService cartService;
  private final UserService userService;

  @PostMapping("/create")
  public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
    try {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      User user = userService.getUser(userDetails.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"));
      Cart cart = cartService.findCart(request.cartId());
      Double amount = cart.getTotalprice();
      PaymentIntent intent = service.createPaymentIntent(amount, request.currency(), user, cart);
      return ResponseEntity.ok(new PaymentResponse(intent.getClientSecret(), intent.getId(), intent.getStatus()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }
}