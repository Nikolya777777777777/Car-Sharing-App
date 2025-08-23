package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    @Positive
    private int inventory;
    @NotNull
    private Type type;
    @NotNull
    @Positive
    private BigDecimal daily_fee;
}
