package com.example.carsharingapp.controller.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Create a new payment", description = "User create a payment as he wants to pay for renting car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment was created successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentResponseDto create(@Valid @RequestBody PaymentRequestDto requestDto) {
        return paymentService.create(requestDto);
    }
}
