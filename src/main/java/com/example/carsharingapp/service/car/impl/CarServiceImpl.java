package com.example.carsharingapp.service.car.impl;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.service.car.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto create(CarRequestDto requestDto) {
        return carMapper.toResponseDto(carRepository.save(carMapper.toModel(requestDto)));
    }
}
