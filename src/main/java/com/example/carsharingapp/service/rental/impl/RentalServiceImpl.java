package com.example.carsharingapp.service.rental.impl;

import com.example.carsharingapp.dto.rental.RentalActiveOrNotActiveRequestDto;
import com.example.carsharingapp.dto.rental.RentalDeciderDto;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDtoWithoutActualReturnDate;
import com.example.carsharingapp.dto.rental.RentalReturnDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.rental.RentalMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.bot.TelegramNotificationService;
import com.example.carsharingapp.service.rental.RentalService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public RentalResponseDtoWithoutActualReturnDate create(RentalRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car was not found with id: "
                        + requestDto.getCarId()));
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        requestDto.setActualReturnDate(null);
        telegramNotificationService.sendNotification(car.getBrand() + " "
                + car.getModel() + " " + "was successfully rented");
        return rentalMapper.toResponseDtoWithoutActualReturnDate(rentalRepository
                .save(rentalMapper.toModel(requestDto)));
    }

    @Override
    public Page<RentalResponseDto> returnCar(Long userId, RentalReturnDto rentalReturnDto,
                                             Pageable pageable) {
        List<Rental> rental = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);
        if (rental.isEmpty()) {
            throw new EntityNotFoundException("Active rentals not found for userId: " + userId);
        }
        List<Long> carIds = rentalReturnDto.getCarIds();
        if (carIds.isEmpty()) {
            throw new EntityNotFoundException("Cars not found for userId: " + userId);
        }
        List<Rental> rentalsToReturn = rental.stream()
                .filter(r -> carIds.contains(r.getCar().getId()))
                .toList();

        rentalsToReturn.forEach(r -> r.setActualReturnDate(LocalDateTime.now()));
        rentalsToReturn.forEach(r -> r.getCar().setInventory(r.getCar().getInventory() + 1));

        List<Rental> saved = rentalRepository.saveAll(rentalsToReturn);

        List<RentalResponseDto> dtos = saved.stream()
                .map(rentalMapper::toResponseDto)
                .toList();
        for (Rental rentalToReturn : rental) {
            telegramNotificationService.sendNotification(rentalToReturn.getCar().getBrand() + " "
                    + rentalToReturn.getCar().getModel() + " " + "was successfully returned");
        }
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    public Page<RentalDeciderDto> returnUserRentals(Long userId, Pageable pageable,
                                                    RentalActiveOrNotActiveRequestDto requestDto) {
        List<Rental> rentals;
        if (requestDto.isActive()) {
            rentals = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);
        } else {
            rentals = rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId);
        }

        if (rentals.isEmpty()) {
            throw new EntityNotFoundException("Any rentals were not found for userId: " + userId);
        }

        if (requestDto.isActive()) {
            List<RentalDeciderDto> result = rentals.stream()
                    .map(rentalMapper::toResponseDtoWithoutActualReturnDate)
                    .map(dto -> (RentalDeciderDto) dto)
                    .toList();
            return new PageImpl<>(result, pageable, result.size());

        } else {
            List<RentalDeciderDto> result = rentals.stream()
                    .map(rentalMapper::toResponseDto)
                    .map(dto -> (RentalDeciderDto) dto)
                    .toList();

            return new PageImpl<>(result, pageable, result.size());
        }
    }

    @Override
    public RentalDeciderDto returnRentalByRentalIdAndUserId(Long userId, Long rentalId) {
        Rental rental = rentalRepository.findByUserIdAndRentalId(userId, rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental was not found with id: "
                        + rentalId));
        if (rental.getActualReturnDate() == null) {
            return rentalMapper.toResponseDtoWithoutActualReturnDate(rental);
        }
        return rentalMapper.toResponseDto(rental);
    }
}
