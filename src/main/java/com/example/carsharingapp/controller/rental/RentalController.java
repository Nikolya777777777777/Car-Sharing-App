package com.example.carsharingapp.controller.rental;

import com.example.carsharingapp.dto.rental.RentalActiveOrNotActiveRequestDto;
import com.example.carsharingapp.dto.rental.RentalDeciderDto;
import com.example.carsharingapp.dto.rental.RentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalResponseDto;
import com.example.carsharingapp.dto.rental.RentalResponseDtoWithoutActualReturnDate;
import com.example.carsharingapp.dto.rental.RentalReturnDto;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.service.rental.RentalService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create a new rental", description = "Creates a new rental")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental was created successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RentalResponseDtoWithoutActualReturnDate create(@Valid @RequestBody
                                                           RentalRequestDto requestDto) {
        return rentalService.create(requestDto);
    }

    @Operation(summary = "Return a car", description = "Return a car"
            + " and set actual actual return date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental was returned successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/return")
    public Page<RentalResponseDto> returnRental(@AuthenticationPrincipal User user, @Valid
                @RequestBody RentalReturnDto rentalReturnDto, Pageable pageable) {
        return rentalService.returnCar(user.getId(), rentalReturnDto, pageable);
    }

    @Operation(summary = "Return all active or not active user's rentals", description = "Return "
            + "all active or not active user's rentals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rentals were returned successfully",
                    content = @Content(schema = @Schema(implementation = RentalDeciderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public Page<RentalDeciderDto> getAllActiveOrNotActiveRentalsByUserId(
            @AuthenticationPrincipal User user, Pageable pageable,
            @RequestBody @Valid RentalActiveOrNotActiveRequestDto requestDto) {
        return rentalService.returnUserRentals(user.getId(), pageable, requestDto);
    }

    @Operation(summary = "Return rental by user Id and rental id", description = "Return "
            + "rental by user Id and rental id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental was returned successfully",
                    content = @Content(schema = @Schema(implementation = RentalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public RentalDeciderDto getRentalByUserIdAndRentalId(@AuthenticationPrincipal User user,
                                                         @PathVariable Long id) {
        return rentalService.returnRentalByRentalIdAndUserId(user.getId(), id);
    }
}
