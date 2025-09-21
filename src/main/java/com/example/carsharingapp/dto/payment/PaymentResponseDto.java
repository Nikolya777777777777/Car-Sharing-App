package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PaymentResponseDto {
    private Long id;
    private Status status;
    private PaymentType type;
    private BigDecimal amountToPay;
    private String sessionId;
    private String sessionUrl;
}
