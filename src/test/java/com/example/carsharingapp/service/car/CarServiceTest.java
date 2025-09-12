package com.example.carsharingapp.service.car;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.car.CarMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.car.CarSpecificationBuilder;
import com.example.carsharingapp.repository.car.CarSpecificationProviderManager;
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
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private CarSpecificationBuilder carSpecificationBuilder;
    @Mock
    private CarSpecificationProviderManager carSpecificationProviderManager;
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
    public void getAllCars_WithPageable_ReturnPageOfCarResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);

        Car car1 = new Car()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarResponseDto carResponseDto1 = new CarResponseDto()
                .setId(car1.getId())
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
                .setInventory(70);

        CarResponseDto carResponseDto2 = new CarResponseDto()
                .setId(car2.getId())
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(800))
                .setType(Type.SEDAN)
                .setInventory(70);

        Page<Car> pageOfAllCars = new PageImpl<>(List.of(car1, car2),  pageable, 2);
        Page<CarResponseDto> expected = new PageImpl<>(List.of(carResponseDto1, carResponseDto2),  pageable, 2);

        when(carRepository.findAll(pageable)).thenReturn(pageOfAllCars);
        when(carMapper.toResponseDto(car1)).thenReturn(carResponseDto1);
        when(carMapper.toResponseDto(car2)).thenReturn(carResponseDto2);

        Page<CarResponseDto> result = carService.getAllCars(pageable);

        assertTrue(EqualsBuilder.reflectionEquals(result, expected));
        verify(carRepository).findAll(pageable);
        verify(carMapper).toResponseDto(car2);
        verify(carMapper).toResponseDto(car1);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }


    @Test
    @DisplayName("""
            Search cars by params
            """)
    public void searchCarsByParams_WithValidRequest_ReturnPageOfCarResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);

        Car car1 = new Car()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarResponseDto carResponseDto1 = new CarResponseDto()
                .setId(car1.getId())
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarSearchParamsDto carSearchParamsDto = new CarSearchParamsDto()
                .setModels(new String[]{"A5"});

        Page<Car> pageOfAllCars = new PageImpl<>(List.of(car1),  pageable, 1);
        Page<CarResponseDto> pageOfAllCarsResponseDto = new PageImpl<>(List.of(carResponseDto1),  pageable, 1);
        Specification<Car> carSpecification = carSpecificationBuilder.build(carSearchParamsDto);

        when(carSpecificationBuilder.build(carSearchParamsDto)).thenReturn(carSpecification);
        when(carRepository.findAll(carSpecification, pageable)).thenReturn(pageOfAllCars);
        when(carMapper.toResponseDto(car1)).thenReturn(carResponseDto1);

        Page<CarResponseDto> result = carService.searchCarsByParams(carSearchParamsDto, pageable);

        assertThat(EqualsBuilder.reflectionEquals(result, pageOfAllCarsResponseDto));
        verify(carSpecificationBuilder, times(2)).build(carSearchParamsDto);
        verify(carRepository).findAll(carSpecification, pageable);
        verify(carMapper).toResponseDto(car1);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

    @Test
    @DisplayName("""
            Update car by request body
            """)
    public void updateCar_ByRequestBody_ReturnCarResponseDto() {
        Car car = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarResponseDto expected = new CarResponseDto()
                .setId(car.getId())
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        Car updatedCar = new Car()
                .setId(car.getId())
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        CarRequestDto carRequestDto = new CarRequestDto()
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carRepository.findByModel(carRequestDto.getModel())).thenReturn(Optional.empty());
        when(carMapper.updateCar(car, carRequestDto)).thenReturn(updatedCar);
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toResponseDto(updatedCar)).thenReturn(expected);

        CarResponseDto actual = carService.update(car.getId(),  carRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(actual, expected));
        verify(carRepository).findById(car.getId());
        verify(carRepository).findByModel(carRequestDto.getModel());
        verify(carMapper).updateCar(car, carRequestDto);
        verify(carRepository).save(updatedCar);
        verify(carMapper).toResponseDto(updatedCar);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

    @Test
    @DisplayName("""
            Update car by invalid id
            """)
    public void updateCar_ByInvalidId_ShouldThrowException() {
        Long carId = 1L;

        CarRequestDto carRequestDto = new CarRequestDto()
                .setBrand("Mercedes")
                .setModel("S-class")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        String expected = "Car was not found with id: " + carId;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        Exception actual = assertThrows(
                EntityNotFoundException.class,
                () -> carService.update(carId, carRequestDto));

        assertThat(actual.getMessage()).isEqualTo(expected);
        verify(carRepository).findById(carId);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

    @Test
    @DisplayName("""
            Delete car by id
            """)
    public void deleteCar_ById_ReturnNothing() {
        Long carId = 1L;

        when(carRepository.existsById(carId)).thenReturn(true);
        carService.deleteById(carId);

        verify(carRepository).existsById(carId);
        verify(carRepository).deleteById(carId);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

    @Test
    @DisplayName("""
            Delete car by invalid id
            """)
    public void deleteCar_ByInvalidId_ShouldThrowException() {
        Long carId = 1L;

        when(carRepository.existsById(carId)).thenReturn(false);
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.deleteById(carId));

        assertThat(exception.getMessage()).isEqualTo("Car was not found with id: " + carId);
        verify(carRepository).existsById(carId);
        verifyNoMoreInteractions(carRepository, carMapper, carSpecificationBuilder);
    }

}
