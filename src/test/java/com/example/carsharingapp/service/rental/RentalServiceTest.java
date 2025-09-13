package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.*;
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

    @Test
    @DisplayName("""
            Return car with not available active Rentals
            """)
    public void returnCar_WithNotActiveRentals_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        String expected = "Active rentals not found for userId: " + user.getId();

        RentalReturnDto rentalReturnDto = new RentalReturnDto()
                .setCarIds(List.of(1L));

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId())).thenReturn(List.of());

        Exception  actual = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnCar(user.getId(), rentalReturnDto, pageable));

        assertThat(actual.getMessage()).isEqualTo(expected);
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNull(user.getId());
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return car with not available Cars
            """)
    public void returnCar_WithNotActiveCars_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        Car car = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        Rental rental = new Rental()
                .setCar(car)
                .setActualReturnDate(null)
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        String expected = "Cars not found for userId: " + user.getId();

        RentalReturnDto rentalReturnDto = new RentalReturnDto()
                .setCarIds(List.of());

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId())).thenReturn(List.of(rental));

        Exception  actual = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnCar(user.getId(), rentalReturnDto, pageable));

        assertThat(actual.getMessage()).isEqualTo(expected);
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNull(user.getId());
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return user's active rentals
            """)
    public void returnActiveUserRentals_WithValidRequestBody_PageOfRentalResponseDto() {
        RentalActiveOrNotActiveRequestDto requestDto = new RentalActiveOrNotActiveRequestDto();
        requestDto.setActive(true);

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
                .setId(1L)
                .setCar(car)
                .setActualReturnDate(null)
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        RentalResponseDtoWithoutActualReturnDate rentalResponseDto = new RentalResponseDtoWithoutActualReturnDate()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(user.getId());

        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalResponseDtoWithoutActualReturnDate> expected = new PageImpl<>(List.of(rentalResponseDto), pageable, 1);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId())).thenReturn(List.of(rental));
        when(rentalMapper.toResponseDtoWithoutActualReturnDate(rental)).thenReturn(rentalResponseDto);

        Page<RentalDeciderDto> actual = rentalService.returnUserRentals(user.getId(), pageable, requestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNull(user.getId());
        verify(rentalMapper).toResponseDtoWithoutActualReturnDate(rental);
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return user's not active rentals
            """)
    public void returnNotActiveUserRentals_WithValidRequestBody_PageOfRentalResponseDto() {
        RentalActiveOrNotActiveRequestDto requestDto = new RentalActiveOrNotActiveRequestDto();
        requestDto.setActive(false);

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
                .setId(1L)
                .setCar(car)
                .setActualReturnDate(LocalDateTime.now().plusDays(1))
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        RentalResponseDto rentalResponseDto = new RentalResponseDto()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setActualReturnDate(LocalDateTime.now().plusDays(1))
                .setUserId(user.getId());

        Pageable pageable = PageRequest.of(0, 10);
        Page<RentalResponseDto> expected = new PageImpl<>(List.of(rentalResponseDto), pageable, 1);

        when(rentalRepository.findByUserIdAndActualReturnDateIsNotNull(user.getId())).thenReturn(List.of(rental));
        when(rentalMapper.toResponseDto(rental)).thenReturn(rentalResponseDto);

        Page<RentalDeciderDto> actual = rentalService.returnUserRentals(user.getId(), pageable, requestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expected,actual));
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNotNull(user.getId());
        verify(rentalMapper).toResponseDto(rental);
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return user's with empty rentals
            """)
    public void returnUserRentals_WithNotAvailableRentalsInDb_ShouldThrowException() {
        RentalActiveOrNotActiveRequestDto requestDto = new RentalActiveOrNotActiveRequestDto();
        requestDto.setActive(false);

        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        Pageable pageable = PageRequest.of(0, 10);
        String expected = "Any rentals were not found for userId: " + user.getId();

        when(rentalRepository.findByUserIdAndActualReturnDateIsNotNull(user.getId())).thenReturn(List.of());

        Exception actual = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnUserRentals(user.getId(), pageable, requestDto));

        assertThat(actual.getMessage()).isEqualTo(expected);
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNotNull(user.getId());
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return not active rental by rental id and user id
            """)
    public void returnNotActiveRental_WithValidRequestBody_ReturnRentalDeciderDto() {
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
                .setId(1L)
                .setCar(car)
                .setActualReturnDate(LocalDateTime.now().plusDays(1))
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        RentalResponseDto rentalResponseDto = new RentalResponseDto()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setActualReturnDate(LocalDateTime.now().plusDays(1))
                .setUserId(user.getId());

        when(rentalRepository.findByUserIdAndRentalId(user.getId(), rental.getId())).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDto(rental)).thenReturn(rentalResponseDto);

        RentalDeciderDto actual = rentalService.returnRentalByRentalIdAndUserId(user.getId(), rental.getId());

        assertTrue(EqualsBuilder.reflectionEquals(rentalResponseDto,actual));
        verify(rentalRepository).findByUserIdAndRentalId(user.getId(), rental.getId());
        verify(rentalMapper).toResponseDto(rental);
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return active rental by rental id and user id
            """)
    public void returnActiveRental_WithValidRequestBody_ReturnRentalDeciderDto() {
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
                .setId(1L)
                .setCar(car)
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setRentalDate(LocalDateTime.now())
                .setUser(user);

        RentalResponseDtoWithoutActualReturnDate rentalResponseDto = new RentalResponseDtoWithoutActualReturnDate()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCarId(1L)
                .setUserId(user.getId());

        when(rentalRepository.findByUserIdAndRentalId(user.getId(), rental.getId())).thenReturn(Optional.of(rental));
        when(rentalMapper.toResponseDtoWithoutActualReturnDate(rental)).thenReturn(rentalResponseDto);

        RentalDeciderDto actual = rentalService.returnRentalByRentalIdAndUserId(user.getId(), rental.getId());

        assertTrue(EqualsBuilder.reflectionEquals(rentalResponseDto,actual));
        verify(rentalRepository).findByUserIdAndRentalId(user.getId(), rental.getId());
        verify(rentalMapper).toResponseDtoWithoutActualReturnDate(rental);
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Return rental by invalid rental id
            """)
    public void returnUserRentals_WithNoValidRentalId_ShouldThrowException() {
        Long rentalId = 1L;

        Long userId = 1L;

        String expected = "Rental was not found with id: " + rentalId;

        when(rentalRepository.findByUserIdAndRentalId(userId, rentalId)).thenReturn(Optional.empty());

        Exception actual = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnRentalByRentalIdAndUserId(userId, rentalId));

        assertThat(actual.getMessage()).isEqualTo(expected);
        verify(rentalRepository).findByUserIdAndRentalId(userId, rentalId);
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper, telegramNotificationService);
    }
}
