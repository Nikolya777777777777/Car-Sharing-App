package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import lombok.Data;
import java.util.List;

@Data
public class PaymentRequestDto {
    private PaymentType type;
    private List<Long> rentalId;
}
