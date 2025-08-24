package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;

public interface RentalService {
    RentalResponseDto create (RentalRequestDto requestDto);
}
