package com.example.carsharingapp.service.car.impl;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.car.CarSpecificationBuilder;
import com.example.carsharingapp.service.car.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarSpecificationBuilder carSpecificationBuilder;

    @Override
    public CarResponseDto create(CarRequestDto requestDto) {
        return carMapper.toResponseDto(carRepository.save(carMapper.toModel(requestDto)));
    }

    @Override
    public Page<CarResponseDto> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::toResponseDto);
    }

    @Override
    public Page<CarResponseDto> searchCarsByParams(CarSearchParamsDto searchParamsDto, Pageable pageable) {
        Specification<Car> carSpecification = carSpecificationBuilder.build(searchParamsDto);
        return null;
    }
}
