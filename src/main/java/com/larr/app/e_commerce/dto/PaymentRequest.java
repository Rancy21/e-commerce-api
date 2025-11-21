package com.larr.app.e_commerce.dto;

import lombok.Data;

@Data
public class PaymentRequest {
  private String cartId;
  private String userId;
  private String token;
  private String payerId;

}