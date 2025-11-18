package com.larr.app.e_commerce.dto;

import lombok.Data;
import com.larr.app.e_commerce.model.CartStatus;

@Data
public class CartUpdateRequest {
  private CartStatus status;
}
