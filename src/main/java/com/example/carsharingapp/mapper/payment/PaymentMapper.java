package com.example.carsharingapp.mapper.payment;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.model.payment.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);
}
