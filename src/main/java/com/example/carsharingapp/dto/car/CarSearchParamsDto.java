package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import lombok.Data;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

@Data
@Accessors(chain=true)
public class CarSearchParamsDto {
    private String[] brands;
    private String[] models;
    private Type type;
    private BigDecimal dailyFee;
}
