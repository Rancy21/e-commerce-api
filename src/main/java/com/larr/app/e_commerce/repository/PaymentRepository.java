package com.larr.app.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.larr.app.e_commerce.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

}
