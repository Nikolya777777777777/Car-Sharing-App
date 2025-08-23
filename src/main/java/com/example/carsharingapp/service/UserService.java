package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.user.UpdateRoleRequestDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
    UserResponseDto updateRoleForUser(Long id, UpdateRoleRequestDto requestDto);
    UserResponseDto getAllInfo(Long userId);
    UserResponseDto updateInformationAboutUser(Long userId, UserRegistrationRequestDto requestDto);
}
