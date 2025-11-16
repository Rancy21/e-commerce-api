package com.larr.app.e_commerce.dto;

import lombok.Data;

@Data
public class ProductUpdateRequest {
  private String name;
  private Double price;
  private String imageUrl;
  private Integer quantity;
}
