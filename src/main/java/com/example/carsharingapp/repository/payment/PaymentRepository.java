package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.model.payment.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    @Query("select p from Payment p join fetch p.rental r where r.user.id = :userId")
    Page<Payment> findByUserId(Long userId, Pageable pageable);
}
