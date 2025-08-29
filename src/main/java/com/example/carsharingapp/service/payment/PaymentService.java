package com.example.carsharingapp.service.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDto create(PaymentRequestDto requestDto);
    void markAsPaid(String sessionId);
    Page<PaymentResponseDto> getPaymentsByUserId(Long userId, Pageable pageable);
}
