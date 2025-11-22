package com.larr.app.e_commerce.dto;

public record CreatePaymentRequest(String cartId,
    String currency) {
}