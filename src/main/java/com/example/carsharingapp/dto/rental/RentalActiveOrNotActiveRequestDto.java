package com.example.carsharingapp.dto.rental;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalActiveOrNotActiveRequestDto {
    private boolean active;
}
