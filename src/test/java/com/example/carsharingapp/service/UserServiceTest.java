package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.user.UpdateRoleRequestDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.exception.RegistrationException;
import com.example.carsharingapp.mapper.user.UserMapper;
import com.example.carsharingapp.model.role.Role;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.model.user.UserServiceImpl;
import com.example.carsharingapp.repository.role.RoleRepository;
import com.example.carsharingapp.repository.user.UserRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.Set;

import static com.example.carsharingapp.model.enums.RoleName.ROLE_CUSTOMER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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
    public void createUser_WithValidRequest_ReturnUserResponseDto() throws RegistrationException {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("nikolya.cr@gmail.com")
                .setPassword("12345678")
                .setRepeatPassword("12345678")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");

        User userBeforeSave = new User()
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        User userAfterSave = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        UserResponseDto expected = new UserResponseDto()
                .setEmail("nikolya.cr@gmail.com")
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(userBeforeSave);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("$2a$10$LLugUeKY.RQnC988wXT2zebftBRFtH0s7uucFS3Mq.t1zhbgQUMYi");
        when(roleRepository.findByName(ROLE_CUSTOMER)).thenReturn(Optional.of(new Role(ROLE_CUSTOMER)));
        when(userRepository.save(userBeforeSave)).thenReturn(userAfterSave);
        when(userMapper.modelToResponseDto(any(User.class))).thenReturn(expected);

        UserResponseDto result = userService.register(requestDto);

        assertTrue(EqualsBuilder.reflectionEquals(result, expected));
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).toModel(requestDto);
        verify(passwordEncoder).encode(requestDto.getPassword());
        verify(roleRepository).findByName(ROLE_CUSTOMER);
        verify(userRepository).save(userBeforeSave);
        verify(userMapper).modelToResponseDto(any(User.class));
        verifyNoMoreInteractions(userRepository,  userMapper,  passwordEncoder,  roleRepository);
    }

    @Test
    @DisplayName("""
            Create a new user with already existing credentials in the DB
            """)
    public void createUser_WithInvalidRequest_ShouldThrowRegistrationException() throws RegistrationException {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("nikolya.cr@gmail.com")
                .setPassword("12345678")
                .setRepeatPassword("12345678")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");


        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("User with this email: "
                + requestDto.getEmail() + " already exist");
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("""
            Update user's role
            """)
    public void updateRoleForUser_WithValidRequest_ReturnUserResponseDto() throws RegistrationException {
        Long id = 1L;

        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto()
                .setRole(ROLE_CUSTOMER);

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        Role role = new Role(ROLE_CUSTOMER)
                .setId(1L);

        UserResponseDto expected = new UserResponseDto()
                .setEmail("nikolya.cr@gmail.com")
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");

        when(roleRepository.findByName(updateRoleRequestDto.getRole())).thenReturn(Optional.ofNullable(role));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.modelToResponseDto(any(User.class))).thenReturn(expected);

        UserResponseDto result = userService.updateRoleForUser(id, updateRoleRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(result, expected));
        verify(roleRepository).findByName(ROLE_CUSTOMER);
        verify(userRepository).findById(id);
        verify(userMapper).modelToResponseDto(user);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository);
    }

    @Test
    @DisplayName("""
            Update user's role, but with invalid role name
            """)
    public void updateUser_WithInvalidRole_ShouldThrowEntityNotFoundException() {
        Long id = 1L;
        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto()
                .setRole(ROLE_CUSTOMER);


        when(roleRepository.findByName(updateRoleRequestDto.getRole())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateRoleForUser(id, updateRoleRequestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Role named: "
                + updateRoleRequestDto.getRole() + " was not found");
        verify(roleRepository).findByName(updateRoleRequestDto.getRole());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("""
            Update user's role, but with invalid user's id
            """)
    public void updateUser_WithInvalidUser_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        UpdateRoleRequestDto updateRoleRequestDto = new UpdateRoleRequestDto()
                .setRole(ROLE_CUSTOMER);

        Role role = new Role(ROLE_CUSTOMER)
                .setId(1L);


        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateRoleForUser(id, updateRoleRequestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("User with id: "
                + id + " was not found");
        verify(roleRepository).findByName(updateRoleRequestDto.getRole());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("""
            Get all information about user
            """)
    public void getAllInformationAboutUser_WithValidId_ReturnUserResponseDto() {
        Long id = 1L;

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false)
                .setRoles(Set.of(new Role(ROLE_CUSTOMER)));

        UserResponseDto expected = new UserResponseDto()
                .setEmail("nikolya.cr@gmail.com")
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        when(userMapper.modelToResponseDto(user)).thenReturn(expected);

        UserResponseDto result = userService.getAllInfo(id);

        assertTrue(EqualsBuilder.reflectionEquals(result, expected));
        verify(userRepository).findById(id);
        verify(userMapper).modelToResponseDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("""
            Get all information about user with invalid id
            """)
    public void getAllInfoAboutUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getAllInfo(id)
        );

        assertThat(exception.getMessage()).isEqualTo("User with id: "
                + id + " was not found");
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("""
            Update information about user
            """)
    public void updateInformationAboutUser_WithValidRequest_ReturnUserResponseDto() {
        Long id = 1L;

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("12345678")
                .setDeleted(false)
                .setRoles(Set.of(new Role(ROLE_CUSTOMER)));

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("orel@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("12345678")
                .setRepeatPassword("12345678");

        User expected = new User()
                .setId(1L)
                .setEmail("orel@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("$2a$10$LLugUeKY.RQnC988wXT2zebftBRFtH0s7uucFS3Mq.t1zhbgQUMYi")
                .setDeleted(false)
                .setRoles(Set.of(new Role(ROLE_CUSTOMER)));

        UserResponseDto expectedResult = new  UserResponseDto()
                .setId(1L)
                .setEmail("orel@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.updateUser(eq(user), eq(requestDto))).thenReturn(expected);
        when(passwordEncoder.encode(anyString())).thenReturn(expected.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(expected);
        when(userMapper.modelToResponseDto(any(User.class))).thenReturn(expectedResult);

        UserResponseDto result = userService.updateInformationAboutUser(id, requestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedResult, result));
        verify(userRepository).findById(id);
        verify(userMapper).updateUser(user, requestDto);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(userMapper).modelToResponseDto(any(User.class));
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("""
            Update information about user with invalid id should throw EntityNotFoundException
            """)
    public void updateInfoAboutUser_WithInvalidId_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("orel@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("12345678")
                .setRepeatPassword("12345678");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateInformationAboutUser(id, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("User with id: "
                + id + " was not found");
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

}
