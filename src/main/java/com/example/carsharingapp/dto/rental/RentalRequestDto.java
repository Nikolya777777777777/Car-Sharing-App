package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

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
