package com.example.carsharingapp.service;

import com.example.carsharingapp.mapper.user.UserMapper;
import com.example.carsharingapp.model.user.UserServiceImpl;
import com.example.carsharingapp.repository.role.RoleRepository;
import com.example.carsharingapp.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Test
    @DisplayName("""
            Create a new user
            """)
    public void createUser_WithValidRequest_ReturnUserResponseDto() {

    }
}
