package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Status status;
    private PaymentType type;
    private BigDecimal amountToPay;
    private String sessionId;
    private String sessionUrl;
}
