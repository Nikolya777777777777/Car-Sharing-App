package com.example.carsharingapp.controller.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.service.payment.PaymentService;
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

    @Operation(summary = "Get payments by user id", description = "Get page of payments By user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payments were got successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Page<PaymentResponseDto> getPaymentByUserId(@PathVariable Long id, Pageable pageable) {
        return paymentService.getPaymentsByUserId(id, pageable);
    }

    @Operation(summary = "Get payment status as successful", description = "Get payment status as successful by session id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment was got successfully",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/success")
    public PaymentStatusResponseDto getPaymentSuccess(@RequestParam String sessionId) {
        return paymentService.getPaymentStatus(sessionId);
    }

    @Operation(summary = "Get payment status as canceled", description = "Get payment status as canceled by session id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payments were got successfully",
                    content = @Content(schema = @Schema(implementation = PaymentStatusResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/cancel")
    public PaymentStatusResponseDto getPaymentCancel(@RequestParam String sessionId) {
        return paymentService.getPaymentStatus(sessionId);
    }
}
