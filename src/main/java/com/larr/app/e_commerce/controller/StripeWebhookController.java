package com.larr.app.e_commerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.service.StripeService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StripeWebhookController {
  private final StripeService service;

  @Value("${stripe.webhook-secret}")
  private String endpointSecret;

  @PostMapping("/webhook/stripe")
  public String handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
    } catch (Exception e) {
      return "Invalid signature";
    }

    if ("payment_intent.succeeded".equals(event.getType())) {
      PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
      service.confirmPayment(intent);
    }

    if ("payment_intent.failed".equals(event.getType())) {
      PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
      service.handleFailedPayment(intent);
    }

    return "ok";
  }

}