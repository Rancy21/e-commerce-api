package com.larr.app.e_commerce.dto;

public record PaymentResponse(String clientSecret, String paymentIntentId, String status) {
}