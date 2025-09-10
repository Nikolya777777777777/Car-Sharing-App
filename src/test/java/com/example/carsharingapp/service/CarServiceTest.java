package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.car.CarSpecificationBuilder;
import com.example.carsharingapp.service.car.impl.CarServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private CarSpecificationBuilder carSpecificationBuilder;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("""
            Create a new car
            """)
    public void createCar_WithValidRequest_ReturnCarResponseDto() {
        CarRequestDto carRequestDto = new CarRequestDto()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        Car car = new Car()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarResponseDto carResponseDto = new CarResponseDto()
                .setId(car.getId())
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        when(carRepository.findByModel(carRequestDto.getModel())).thenReturn(Optional.of(car));
        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car.setId(1L));
        when(carMapper.toResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.create(carRequestDto);

        assertThat(EqualsBuilder.reflectionEquals(result, carResponseDto));
        verify(carRepository).findByModel(carRequestDto.getModel());
        verify(carMapper).toModel(carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toResponseDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("""
            Create a new car with car model which is not available in inventory
            """)
    public void createCar_WithInValidRequest_ShouldThrowException() {
        CarRequestDto carRequestDto = new CarRequestDto()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);


        when(carRepository.findByModel(carRequestDto.getModel())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> carService.create(carRequestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("There is no car with the given model " +  carRequestDto.getModel());
        verify(carRepository).findByModel(carRequestDto.getModel());
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

    @Test
    @DisplayName("""
            Get all cars
            """)
    public void getAllCars_WithValidRequest_ReturnPageOfCarResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);

        Car car1 = new Car()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        Car car2 = new Car()
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(800))
                .setType(Type.SEDAN)
                .setInventory(20);

        CarResponseDto carResponseDto1 = new CarResponseDto()
                .setId(car1.getId())
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarResponseDto carResponseDto2 = new CarResponseDto()
                .setId(car2.getId())
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(800))
                .setType(Type.SEDAN)
                .setInventory(20);

        Page<Car> pageOfAllCars = new PageImpl<>(List.of(car1, car2),  pageable, 2);
        Page<CarResponseDto> pageOfAllCarsResponseDto = new PageImpl<>(List.of(carResponseDto1, carResponseDto2),  pageable, 2);

        when(carRepository.findAll(pageable)).thenReturn(pageOfAllCars);
        when(carMapper.toResponseDto(car1)).thenReturn(carResponseDto1);
        when(carMapper.toResponseDto(car2)).thenReturn(carResponseDto2);

        Page<CarResponseDto> result = carService.getAllCars(pageable);

        assertThat(EqualsBuilder.reflectionEquals(result, pageOfAllCarsResponseDto));
        verify(carRepository).findAll(pageable);
        verify(carMapper).toResponseDto(car1);
        verify(carMapper).toResponseDto(car2);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

}
