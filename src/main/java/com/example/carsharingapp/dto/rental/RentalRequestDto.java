package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalRequestDto {
    @NotNull
    private LocalDateTime rentalDate;
    @NotNull
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    @NotNull
    private Long carId;
    @NotNull
    private Long userId;
}
