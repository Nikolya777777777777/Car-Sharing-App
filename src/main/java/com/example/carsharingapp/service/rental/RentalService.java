package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.RentalActiveOrNotActiveRequestDto;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalReturnDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto create (RentalRequestDto requestDto);
    Page<RentalResponseDto> returnCar(Long userId, RentalReturnDto rentalReturnDto, Pageable pageable);
    Page<RentalResponseDto> returnUserRentals(Long userId, Pageable pageable, RentalActiveOrNotActiveRequestDto requestDto);
    RentalResponseDto returnRentalByRentalIdAndUserId(Long userId, Long rentalId, Pageable pageable);
}
