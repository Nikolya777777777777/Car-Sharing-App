package com.example.carsharingapp.repository;

import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.car.CarSpecificationBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTest {
    private final CarSpecificationBuilder carSpecificationBuilder = new CarSpecificationBuilder();

    @Autowired
    private CarRepository carRepository;

    @Test
    @DisplayName("""
            Find all cars with filter and pageable object-
            """)
    @Sql(scripts = {
            "classpath:database/repository/car/add-two-cars-to-cars-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/car/truncate-car-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllCars_WithFilterAndPageable_ReturnPageOfCars() {
        Pageable pageable = PageRequest.of(0, 10);
        CarSearchParamsDto carSearchParamsDto = new CarSearchParamsDto();
        carSearchParamsDto.setType(Type.SEDAN);
        Specification<Car> carSpecification = carSpecificationBuilder.build(carSearchParamsDto);
        Car car = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setInventory(50)
                .setDeleted(false);
        Page<Car> expected = new PageImpl<>(List.of(car),  pageable, 1);
        Page<Car> actual =  carRepository.findAll(carSpecification, pageable);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));

    }
}
