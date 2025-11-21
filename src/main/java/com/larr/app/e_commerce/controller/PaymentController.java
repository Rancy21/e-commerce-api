package com.larr.app.e_commerce.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.dto.PaymentRequest;
import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.security.service.CartService;
import com.larr.app.e_commerce.service.PaymentService;
import com.larr.app.e_commerce.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService service;
  private final CartService cartService;
  private final UserService userService;

  @PostMapping("/create")
  public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
    try {
      // Fetch entities from database
      Cart cart = cartService.findCart(request.getCartId());
      User user = userService.getUserById(request.getUserId());
      Double amount = cart.getTotalprice();

      String currency = "USD";

      String approvalLink = service.createPayment(amount, currency, user, cart);

      return ResponseEntity.ok(approvalLink);
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Paypal Error");
    }
  }

  /**
   * Paypal redirects here with a token
   */

  @GetMapping("/success")
  public ResponseEntity<?> successPay(@RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
    try {
      service.complPayment(token);
      return ResponseEntity.ok("Payment completed successfully!");
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Payment capture failed");
    }
  }
}