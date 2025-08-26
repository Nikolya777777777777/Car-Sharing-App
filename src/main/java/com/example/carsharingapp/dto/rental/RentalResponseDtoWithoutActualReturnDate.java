package com.example.carsharingapp.dto.rental;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalResponseDtoWithoutActualReturnDate extends RentalDeciderDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private Long carId;
    private Long userId;
}
