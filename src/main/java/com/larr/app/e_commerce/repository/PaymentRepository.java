package com.larr.app.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.larr.app.e_commerce.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
