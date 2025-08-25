package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalActiveOrNotActiveRequestDto {
    @NotNull
    private boolean is_active;
}
