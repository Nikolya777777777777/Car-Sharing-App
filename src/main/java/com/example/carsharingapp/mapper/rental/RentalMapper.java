package com.example.carsharingapp.mapper.rental;

import com.example.carsharingapp.config.mapper.MapperConfig;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDtoWithoutActualReturnDate;
import com.example.carsharingapp.model.rental.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car.id", source = "carId")
    @Mapping(target = "user.id", source = "userId")
    Rental toModel(RentalRequestDto rentalRequestDto);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toResponseDto(Rental rental);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDtoWithoutActualReturnDate toResponseDtoWithoutActualReturnDate(Rental rental);
}
