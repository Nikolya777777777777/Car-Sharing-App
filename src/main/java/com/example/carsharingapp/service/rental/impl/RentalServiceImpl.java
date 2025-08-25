package com.example.carsharingapp.service.rental.impl;

import com.example.carsharingapp.dto.rental.RentalActiveOrNotActiveRequestDto;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalReturnDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.rental.RentalMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.repository.car.CarRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    @Override
    public RentalResponseDto create(RentalRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car was not found with id: " + requestDto.getCarId()));
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        requestDto.setActualReturnDate(null);
        return rentalMapper.toResponseDto(rentalRepository.save(rentalMapper.toModel(requestDto)));
    }

    @Override
    public Page<RentalResponseDto> returnCar(Long userId, RentalReturnDto rentalReturnDto, Pageable pageable) {
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

        List<Rental> saved = rentalRepository.saveAll(rentalsToReturn);

        List<RentalResponseDto> dtos = saved.stream()
                .map(rentalMapper::toResponseDto)
                .toList();

        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    public Page<RentalResponseDto> returnUserRentals(Long userId, Pageable pageable, RentalActiveOrNotActiveRequestDto requestDto) {
        List<Rental> rentals = new ArrayList<>();
        if (requestDto.is_active()) {
            rentals = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);
        } else {
            rentals = rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId);
        }

        if (rentals.isEmpty()) {
            throw new EntityNotFoundException("Any rentals were not found for userId: " + userId);
        }

        List<RentalResponseDto> result = rentals.stream()
                .map(rentalMapper::toResponseDto)
                .toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public RentalResponseDto returnRentalByRentalIdAndUserId(Long userId, Long rentalId, Pageable pageable) {
        Rental rental = rentalRepository.findByUserIdAndRentalId(userId, rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental was not found with id: " + rentalId));
        return rentalMapper.toResponseDto(rental);
    }
}
