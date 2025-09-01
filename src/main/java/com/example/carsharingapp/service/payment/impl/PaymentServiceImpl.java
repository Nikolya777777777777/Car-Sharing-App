package com.example.carsharingapp.service.payment.impl;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.payment.PaymentMapper;
import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.example.carsharingapp.model.payment.Payment;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.payment.PaymentService;
import com.example.carsharingapp.service.stripe.StripeService;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String PAID = "paid";
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @Transactional
    @Override
    public PaymentResponseDto create(PaymentRequestDto request) {
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental was not found with id: " + request.getRentalId()
                ));

        BigDecimal totalAmount = calculateAmount(rental, request.getType());
        Session session = stripeService.createRentalPaymentSession(rental, totalAmount);

        Payment payment = new Payment();
        payment.setStatus(Status.PENDING);
        payment.setType(request.getType());
        payment.setAmountToPay(totalAmount);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setRental(rental);

        paymentRepository.save(payment);

        return paymentMapper.toResponseDto(payment);
    }

    private BigDecimal calculateAmount(Rental rental, PaymentType type) {
        if (type == PaymentType.PAYMENT) {
            long days = Duration.between(rental.getRentalDate(), rental.getReturnDate()).toDays();
            if (days == 0) {
                days = 1;
            }
            return rental.getCar().getDaily_fee().multiply(BigDecimal.valueOf(days));
        } else {
            long days = Duration.between(rental.getReturnDate(), rental.getActualReturnDate()).toDays();
            if (days == 0) {
                days = 1;
            }
            return rental.getCar().getDaily_fee().multiply(BigDecimal.valueOf(days));
        }
    }

    @Override
    public PaymentStatusResponseDto getPaymentStatus(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment was not found with session id: " + sessionId
                ));

        if (PAID.equals(stripeService.checkPaymentStatus(sessionId))) {
            payment.setStatus(Status.PAID);
        } else {
            payment.setStatus(Status.PENDING);
        }

        paymentRepository.save(payment);

        return new PaymentStatusResponseDto(payment.getStatus());
    }

    @Override
    public Page<PaymentResponseDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(paymentMapper::toResponseDto);
    }
}
