package com.example.carsharingapp.controller.car;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "Create a new car", description = "Creates a new car (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = @Content(schema = @Schema(implementation = CarResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CarResponseDto create(@Valid @RequestBody CarRequestDto requestDto) {
        return carService.create(requestDto);
    }
//
//    @Operation(summary = "Get all available cars", description = "Returns pages of all cars")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Car was found"),
//            @ApiResponse(responseCode = "404", description = "Car was not found")
//    })
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping
//    public Page<CarResponseDto> getAllCars(Pageable pageable) {
//        return carService.getAllCars(pageable);
//    }

//    @GetMapping
//    public Page<CarResponseDto> getCarByDetails(CarSearchParamsDto searchParamsDto, Pageable pageable) {
//        return carService.searchCarsByParams(searchParamsDto, pageable);
//    }
}
