package com.example.carsharingapp.repository.car;

import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.repository.car.spec.BrandSpecificationProvider;
import com.example.carsharingapp.repository.car.spec.DailyFeeSpecificationProvider;
import com.example.carsharingapp.repository.car.spec.ModelSpecificationProvider;
import com.example.carsharingapp.repository.car.spec.TypeSpecificationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CarSpecificationBuilder.class,
        CarSpecificationBuilder.class,
        CarSpecificationProviderManager.class,
        BrandSpecificationProvider.class,
        ModelSpecificationProvider.class,
        DailyFeeSpecificationProvider.class,
        TypeSpecificationProvider.class})
public class CarRepositoryTest {
    @Autowired
    private CarSpecificationBuilder carSpecificationBuilder;

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
                .setDailyFee(BigDecimal.valueOf(700.00))
                .setInventory(50)
                .setType(Type.SEDAN)
                .setDeleted(false);
        Page<Car> expected = new PageImpl<>(List.of(car),  pageable, 1);
        Page<Car> actual =  carRepository.findAll(carSpecification, pageable);

        assertEquals(expected.getContent().get(0).getBrand(), actual.getContent().get(0).getBrand());
        assertEquals(expected.getContent().get(0).getModel(), actual.getContent().get(0).getModel());
        assertEquals(expected.getContent().get(0).getDailyFee().setScale(2), actual.getContent().get(0).getDailyFee().setScale(2));
        assertEquals(expected.getContent().get(0).getInventory(), actual.getContent().get(0).getInventory());
        assertEquals(expected.getContent().get(0).getType(), actual.getContent().get(0).getType());
        assertEquals(expected.getContent().get(0).isDeleted(), actual.getContent().get(0).isDeleted());
        assertEquals(expected.getContent().get(0).getId(), actual.getContent().get(0).getId());
        assertFalse(actual.getContent().get(0).isDeleted());
    }

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
    public void findCar_ByModel_ReturnCar() {
        Car expected = new Car()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700.00))
                .setInventory(50)
                .setType(Type.SEDAN)
                .setDeleted(false);
        Optional<Car> actual =  carRepository.findByModel(expected.getModel());

        assertEquals(expected.getBrand(), actual.get().getBrand());
        assertEquals(expected.getModel(), actual.get().getModel());
        assertEquals(expected.getDailyFee().setScale(2), actual.get().getDailyFee().setScale(2));
        assertEquals(expected.getInventory(), actual.get().getInventory());
        assertEquals(expected.getType(), actual.get().getType());
        assertEquals(expected.isDeleted(), actual.get().isDeleted());
        assertEquals(expected.getId(), actual.get().getId());
        assertFalse(actual.get().isDeleted());
    }
}
