package com.larr.app.e_commerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.Payment;
import com.larr.app.e_commerce.model.PaymentStatus;
import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

  private final PaymentRepository repository;

  @Value("${stripe.redirect.success}")
  private String successUrl;

  @Value("${stripe.redirect.cancel}")
  private String cancelUrl;

  @Transactional
  public PaymentIntent createPaymentIntent(Double amount, String currency, User user, Cart cart)
      throws StripeException {
    // Convert Amount to Cents
    long amountInCents = (long) (amount * 100);

    // Build the Stripe payment intent Parameters
    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
        .setAmount(amountInCents)
        .setCurrency(currency.toLowerCase())
        .setAutomaticPaymentMethods(
            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                .setEnabled(true).build())
        .build();

    PaymentIntent intent = PaymentIntent.create(params);

    Payment payment = new Payment();
    payment.setCart(cart);
    payment.setUser(user);
    payment.setMethod("stripe");
    payment.setCurrency(currency);
    payment.setTransactionId(intent.getId());
    payment.setStatus(PaymentStatus.pending);
    payment.setProviderResponse(intent.toJson());

    repository.save(payment);

    return intent;
  }

  @Transactional
  public void confirmPayment(PaymentIntent intent) {
    Payment payment = repository.findByTransactionId(intent.getId())
        .orElseThrow(() -> new RuntimeException("Payment not found"));
    payment.setStatus(PaymentStatus.completed);
    payment.setProviderResponse(intent.toJson());
    repository.save(payment);

  }

  @Transactional
  public void handleFailedPayment(PaymentIntent intent) {
    Payment payment = repository.findByTransactionId(intent.getId())
        .orElseThrow(() -> new RuntimeException("Payment not found"));

    payment.setStatus(PaymentStatus.failed);
    repository.save(payment);
  }
}