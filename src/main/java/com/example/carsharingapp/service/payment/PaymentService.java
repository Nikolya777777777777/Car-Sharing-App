package com.example.carsharingapp.service.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto create(PaymentRequestDto requestDto);
}
