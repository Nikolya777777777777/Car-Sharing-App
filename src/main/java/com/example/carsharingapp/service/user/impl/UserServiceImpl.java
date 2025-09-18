package com.example.carsharingapp.service.user.impl;

import com.example.carsharingapp.dto.user.UpdateRoleRequestDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.exception.RegistrationException;
import com.example.carsharingapp.mapper.user.UserMapper;
import com.example.carsharingapp.model.enums.RoleName;
import com.example.carsharingapp.model.role.Role;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.repository.role.RoleRepository;
import com.example.carsharingapp.repository.user.UserRepository;
import com.example.carsharingapp.service.user.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with this email: "
                    + requestDto.getEmail() + " already exist");
        }
        User userToSave = userMapper.toModel(requestDto);
        userToSave.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role "
                        + RoleName.ROLE_CUSTOMER + " was not found"));

        userToSave.setRoles(Set.of(userRole));
        userRepository.save(userToSave);
        return userMapper.modelToResponseDto(userToSave);
    }

    @Override
    public UserResponseDto updateRoleForUser(Long id, UpdateRoleRequestDto requestDto) {
        Role role = roleRepository.findByName(requestDto.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role named: "
                        + requestDto.getRole() + " was not found"));
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id: "
                        + id + " was not found"));
        if (user.getRoles().contains(role)) {
            return userMapper.modelToResponseDto(userRepository.save(user));
        }
        user.setRoles(new HashSet<>(Set.of(role)));
        return userMapper.modelToResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getAllInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: "
                        + userId + " was not found"));
        return userMapper.modelToResponseDto(user);
    }

    @Override
    public UserResponseDto updateInformationAboutUser(Long userId,
                                                      UserRegistrationRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: "
                        + userId + " was not found"));
        User updatedUser = userMapper.updateUser(user, requestDto);
        updatedUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        return userMapper.modelToResponseDto(userRepository.save(updatedUser));
    }
}
