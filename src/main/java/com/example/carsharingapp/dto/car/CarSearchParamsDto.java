package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import java.math.BigDecimal;

public record CarSearchParamsDto(String[] brands, String[] models, Type type, BigDecimal dailyFee) {
}
