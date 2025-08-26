package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDtoWithoutActualReturnDate create (RentalRequestDto requestDto);
    Page<RentalResponseDto> returnCar(Long userId, RentalReturnDto rentalReturnDto, Pageable pageable);
    Page<RentalDeciderDto> returnUserRentals(Long userId, Pageable pageable, RentalActiveOrNotActiveRequestDto requestDto);
    RentalDeciderDto returnRentalByRentalIdAndUserId(Long userId, Long rentalId, Pageable pageable);
}
