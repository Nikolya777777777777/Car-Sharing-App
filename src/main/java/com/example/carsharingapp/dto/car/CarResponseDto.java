package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private int inventory;
    private Type type;
    private BigDecimal dailyFee;
}
