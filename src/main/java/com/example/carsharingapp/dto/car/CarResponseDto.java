package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private int inventory;
    private Type type;
    private BigDecimal daily_fee;
}
