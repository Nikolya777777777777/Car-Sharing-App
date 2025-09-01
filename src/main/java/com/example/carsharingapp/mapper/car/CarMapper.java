package com.example.carsharingapp.mapper.car;

import com.example.carsharingapp.config.mapper.MapperConfig;
import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.model.car.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CarRequestDto carRequestDto);

    CarResponseDto toResponseDto(Car car);

    Car updateCar(@MappingTarget Car car, CarRequestDto carRequestDto);
}
