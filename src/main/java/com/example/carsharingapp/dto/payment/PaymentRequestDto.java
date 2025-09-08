package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull
    private PaymentType type;
    @NotNull
    @Positive
    private Long rentalId;
}
