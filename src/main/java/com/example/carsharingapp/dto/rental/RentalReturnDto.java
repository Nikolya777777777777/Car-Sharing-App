package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalReturnDto {
    @NotNull
    private List<Long> carIds;
}
