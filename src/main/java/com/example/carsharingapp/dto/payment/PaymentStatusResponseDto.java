package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PaymentStatusResponseDto {
    private Status status;
}
