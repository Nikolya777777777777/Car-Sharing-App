package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import lombok.Data;

@Data
public class PaymentRequestDto {
    private PaymentType type;
    private Long rentalId;
}
