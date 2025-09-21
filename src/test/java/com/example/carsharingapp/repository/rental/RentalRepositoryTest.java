package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.model.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;
    @Test
    @DisplayName("""
            Find rental by user id and actual return date null
            """)
    @Sql(scripts = {
            "classpath:database/repository/rental/add/add-user-to-users-table.sql",
            "classpath:database/repository/rental/add/add-car-to-cars-table.sql",
            "classpath:database/repository/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/repository/rental/truncate/truncate-cars-table.sql",
            "classpath:database/repository/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findRental_ByUserIdAndActualReturnDateNull_ReturnListOfRentals() {
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
                .setId(2L)
                .setCar(car)
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 14, 30, 0))
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 14, 30, 0))
                .setUser(user);

        List<Rental> actual = rentalRepository.findByUserIdAndActualReturnDateIsNull(user.getId());
        List<Rental> expected = List.of(rental);

        assertTrue(EqualsBuilder.reflectionEquals(actual.get(0), expected.get(0), "car", "user"));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("""
            Find rental by user id and actual return date not null
            """)
    @Sql(scripts = {
            "classpath:database/repository/rental/add/add-user-to-users-table.sql",
            "classpath:database/repository/rental/add/add-car-to-cars-table.sql",
            "classpath:database/repository/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/repository/rental/truncate/truncate-cars-table.sql",
            "classpath:database/repository/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findRental_ByUserIdAndRentalId_OptionalOfRental() {
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
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 14, 30, 0))
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 14, 30, 0))
                .setActualReturnDate(LocalDateTime.of(2025, 8, 30, 14, 38, 2))
                .setUser(user);

        Optional<Rental> actual = rentalRepository.findByUserIdAndRentalId(user.getId(), rental.getId());
        Optional<Rental> expected = Optional.of(rental);


        assertTrue(EqualsBuilder.reflectionEquals(actual.get(), expected.get(), "car", "user"));
    }

    @Test
    @DisplayName("""
            Find rental by user id and actual return date not null
            """)
    @Sql(scripts = {
            "classpath:database/repository/rental/add/add-user-to-users-table.sql",
            "classpath:database/repository/rental/add/add-car-to-cars-table.sql",
            "classpath:database/repository/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/repository/rental/truncate/truncate-cars-table.sql",
            "classpath:database/repository/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findRental_ByUserIdAndRentalId_ReturnListOfRentals() {
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
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 14, 30, 0))
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 14, 30, 0))
                .setActualReturnDate(LocalDateTime.of(2025, 8, 30, 14, 38, 2))
                .setUser(user);

        Optional<Rental> actual = rentalRepository.findByUserIdAndRentalId(user.getId(), rental.getId());
        Optional<Rental> expected = Optional.of(rental);

        assertTrue(EqualsBuilder.reflectionEquals(actual.get(), expected.get(), "car", "user"));
    }
}
