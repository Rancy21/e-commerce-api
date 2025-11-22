package com.larr.app.e_commerce.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.larr.app.e_commerce.model.Cart;
import com.larr.app.e_commerce.model.Payment;
import com.larr.app.e_commerce.model.PaymentStatus;
import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.repository.PaymentRepository;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayPalService {

  private final PayPalHttpClient payPalHttpClient;
  private final PaymentRepository repository;
  private final Gson gson = new Gson();

  @Value("${paypal.redirect.success}")
  private String successUrl;

  @Value("${paypal.redirect.cancel}")
  private String cancelUrl;

  /**
   * Create a Payment Order in Paypal and save initial entity as pending
   */

  @Transactional
  public String createPayment(Double totalAmount, String currency, User user, Cart cart) throws IOException {
    // Build the paypal request
    OrderRequest orderRequest = new OrderRequest();

    orderRequest.checkoutPaymentIntent("CAPTURE");

    // Application context(return urls)

    ApplicationContext applicationContext = new ApplicationContext()
        .brandName("E-Commerce")
        .landingPage("BILLING")
        .cancelUrl(cancelUrl)
        .returnUrl(successUrl)
        .userAction("PAY_NOW");

    orderRequest.applicationContext(applicationContext);

    // Purchase Unit (Amount)
    List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
    PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
        .referenceId(cart.getId())
        .description("Purchase from E-commerce App")
        .amountWithBreakdown(
            new AmountWithBreakdown().currencyCode(currency).value(String.format("%.2f", totalAmount)));
    purchaseUnitRequests.add(purchaseUnitRequest);
    orderRequest.purchaseUnits(purchaseUnitRequests);

    // Call the PayPal API
    OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
    HttpResponse<Order> response = payPalHttpClient.execute(request);
    Order order = response.result();

    // Save to Database using Payment Entity
    Payment payment = new Payment();

    payment.setCart(cart);
    payment.setUser(user);
    payment.setCurrency(currency);
    payment.setMethod("PAYPAL");
    payment.setStatus(PaymentStatus.pending);
    payment.setTransactionId(order.id());
    payment.setProviderResponse(gson.toJson(order));

    repository.save(payment);

    // Return the approval Link to the frontend so that it redirect the user to this
    // URL
    return order.links().stream().filter(link -> "approve".equals(link.rel())).findFirst()
        .orElseThrow(() -> new NoSuchElementException("No approval link found")).href();
  }

  /**
   * Capture the payment after the user approves it on PayPal
   */
  @Transactional
  public Payment complPayment(String token) throws IOException {
    // the token is the order ID returned by paypal in the success url

    OrdersCaptureRequest request = new OrdersCaptureRequest(token);
    HttpResponse<Order> response = payPalHttpClient.execute(request);
    Order order = response.result();

    // Find the local payment record
    Payment payment = repository.findByTransactionId(token)
        .orElseThrow(() -> new RuntimeException("Payment not found in system"));

    if ("COMPLETED".equals(order.status())) {
      payment.setStatus(PaymentStatus.completed);
      payment.setProviderResponse(gson.toJson(order));
    } else {
      payment.setStatus(PaymentStatus.failed);
    }

    return repository.save(payment);
  }

}