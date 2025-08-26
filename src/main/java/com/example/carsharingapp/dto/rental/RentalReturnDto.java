package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RentalReturnDto {
    @NotNull
    private List<Long> carIds;
}
