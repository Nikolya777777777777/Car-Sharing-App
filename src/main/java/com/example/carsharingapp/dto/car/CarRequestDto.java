package com.example.carsharingapp.dto.car;

import com.example.carsharingapp.model.enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @Positive
    private int inventory;
    @NotNull
    private Type type;
    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
