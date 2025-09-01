package com.example.carsharingapp.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalResponseDtoWithoutActualReturnDate extends RentalDeciderDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private Long carId;
    private Long userId;
}
