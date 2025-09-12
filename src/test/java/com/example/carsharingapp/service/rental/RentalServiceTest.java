package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDtoWithoutActualReturnDate;
import com.example.carsharingapp.dto.rental.RentalReturnDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.rental.RentalMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.bot.TelegramNotificationService;
import com.example.carsharingapp.service.rental.impl.RentalServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("""
            Create a new Rental
            """)
    public void createRental_WithValidRequestBody_ReturnRentalResponseDto() {
        Car car = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        User user = new User()
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        RentalRequestDto rentalRequestDto = new RentalRequestDto()
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(user.getId());

        Rental rental = new Rental()
                .setCar(car)
                .setActualReturnDate(rentalRequestDto.getActualReturnDate())
                .setRentalDate(rentalRequestDto.getRentalDate())
                .setReturnDate(rentalRequestDto.getReturnDate())
                .setUser(user);

        RentalResponseDtoWithoutActualReturnDate expected = new RentalResponseDtoWithoutActualReturnDate()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(user.getId());

        when(carRepository.findById(rentalRequestDto.getCarId())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalMapper.toModel(rentalRequestDto)).thenReturn(rental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental.setId(1L));
        when(rentalMapper.toResponseDtoWithoutActualReturnDate(rental)).thenReturn(expected);

        RentalResponseDtoWithoutActualReturnDate actual = rentalService.create(rentalRequestDto);
        assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
        verify(carRepository).findById(rentalRequestDto.getCarId());
        verify(carRepository).save(any(Car.class));
        verify(telegramNotificationService).sendNotification(anyString());
        verify(rentalMapper).toModel(rentalRequestDto);
        verify(rentalRepository).save(any(Rental.class));
        verify(rentalMapper).toResponseDtoWithoutActualReturnDate(rental);
        verifyNoMoreInteractions(carRepository,rentalRepository,rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Create a new Rental with invalid id 
            """)
    public void createRental_WithInValidCarId_ShouldThrowException() {
        RentalRequestDto rentalRequestDto = new RentalRequestDto()
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(1L);

        String expected =  "Car was not found with id: " + rentalRequestDto.getCarId();

        when(carRepository.findById(rentalRequestDto.getCarId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.create(rentalRequestDto));

        assertThat(exception.getMessage()).isEqualTo(expected);
        verify(carRepository).findById(rentalRequestDto.getCarId());
        verifyNoMoreInteractions(carRepository,rentalRepository,rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return car
            """)
    public void returnCar_WithValidRequestBody_PageOfRentalResponseDto() {
        Car car = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        Rental rental = new Rental()
                .setCar(car)
                .setActualReturnDate(null)
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        RentalReturnDto rentalReturnDto = new RentalReturnDto()
                .setCarIds(List.of(1L));

        RentalResponseDto rentalResponseDto = new RentalResponseDto()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setActualReturnDate(LocalDateTime.now().plusDays(1))
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(user.getId());

        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalResponseDto> expected = new PageImpl<>(List.of(rentalResponseDto), pageable, 1);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId())).thenReturn(List.of(rental));
        when(rentalRepository.saveAll(List.of(rental))).thenReturn(List.of(rental.setId(1L)));
        when(rentalMapper.toResponseDto(rental.setId(1L))).thenReturn(rentalResponseDto);

        Page<RentalResponseDto> actual = rentalService.returnCar(user.getId(), rentalReturnDto, pageable);
        assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNull(user.getId());
        verify(rentalRepository).saveAll(List.of(rental));
        verify(telegramNotificationService).sendNotification(anyString());
        verify(rentalMapper).toResponseDto(rental.setId(1L));
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }
}
