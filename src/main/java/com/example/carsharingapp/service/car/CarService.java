package com.example.carsharingapp.service.car;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto create(CarRequestDto requestDto);

    Page<CarResponseDto> getAllCars(Pageable pageable);

    Page<CarResponseDto> searchCarsByParams(CarSearchParamsDto searchParamsDto, Pageable pageable);

    CarResponseDto update(Long id, CarRequestDto requestDto);

    void deleteById(Long id);
}
