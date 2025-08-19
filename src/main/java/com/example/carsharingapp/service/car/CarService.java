package com.example.carsharingapp.service.car;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;

public interface CarService {
    CarResponseDto create(CarRequestDto requestDto);
}
