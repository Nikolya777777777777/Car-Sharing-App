package com.example.carsharingapp.mapper.user;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel (UserRegistrationRequestDto requestDto);
    UserResponseDto modelToResponseDto (User user);
    User updateUser(@MappingTarget User user, UserRegistrationRequestDto requestDto);
}
