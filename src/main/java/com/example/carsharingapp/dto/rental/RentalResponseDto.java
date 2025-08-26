package com.example.carsharingapp.dto.rental;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalResponseDto extends RentalDeciderDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Long carId;
    private Long userId;
}
