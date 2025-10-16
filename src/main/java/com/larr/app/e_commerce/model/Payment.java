package com.larr.app.e_commerce.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String currency;
    private PaymentStatus status;
    private String method;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
