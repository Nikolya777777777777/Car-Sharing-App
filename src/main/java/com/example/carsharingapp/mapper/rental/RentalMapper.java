package com.example.carsharingapp.mapper.rental;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.model.rental.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    Rental toModel (RentalRequestDto rentalRequestDto);

    RentalResponseDto toResponseDto (Rental rental);
}
