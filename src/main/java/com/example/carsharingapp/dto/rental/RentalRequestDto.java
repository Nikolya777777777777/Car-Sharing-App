package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalRequestDto {
    @NotBlank
    private LocalDateTime rentalDate;
    @NotBlank
    private LocalDateTime returnDate;
    @NotBlank
    private LocalDateTime actualReturnDate;
    @NotNull
    private Long carId;
    @NotNull
    private Long userId;
}
