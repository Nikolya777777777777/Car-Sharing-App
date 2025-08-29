package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.model.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
}
