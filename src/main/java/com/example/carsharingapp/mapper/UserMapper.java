package com.example.carsharingapp.mapper;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.UserResponseDto;
import com.example.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel (UserRegistrationRequestDto requestDto);
    UserResponseDto modelToResponseDto (User user);
}
