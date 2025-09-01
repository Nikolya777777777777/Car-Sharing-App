package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class RentalReturnDto {
    @NotNull
    private List<Long> carIds;
}
