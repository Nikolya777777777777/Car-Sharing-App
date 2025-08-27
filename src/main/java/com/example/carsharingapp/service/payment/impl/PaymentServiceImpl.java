package com.example.carsharingapp.service.payment.impl;

import com.example.carsharingapp.client.StripeClient;
import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.StripeSessionResponse;
import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.example.carsharingapp.model.payment.Payment;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeClient stripeClient;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Transactional
    public PaymentResponseDto create(PaymentRequestDto request) {
        List<Rental> rentals = rentalRepository.findAllById(request.getRentalId());

        if (rentals.isEmpty()) {
            throw new RuntimeException("No rentals found for provided IDs");
        }

        BigDecimal totalAmount = rentals.stream()
                .map(rental -> calculateAmount(rental, request.getType()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long amountInCents = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();

        StripeSessionResponse session = stripeClient.createCheckoutSession(
                amountInCents,
                "usd",
                successUrl,
                cancelUrl
        );

        Payment payment = new Payment();
        payment.setStatus(Status.PENDING);
        payment.setType(request.getType());
        payment.setAmountToPay(totalAmount);
        payment.setSessionId(session.id());
        payment.setSessionUrl(session.url());

        // для поля rental можна зберігати першу оренду або змінити Payment на ManyToMany
        payment.setRental(rentals.get(0));

        paymentRepository.save(payment);

        return new PaymentResponseDto(
                payment.getId(),
                payment.getStatus(),
                payment.getType(),
                payment.getAmountToPay(),
                payment.getSessionId(),
                payment.getSessionUrl()
        );
    }

    private BigDecimal calculateAmount(Rental rental, PaymentType type) {
        if (type == PaymentType.PAYMENT) {
            long days = Duration.between(rental.getRentalDate(), rental.getReturnDate()).toDays();
            if (days == 0) days = 1;
            return rental.getCar().getDaily_fee().multiply(BigDecimal.valueOf(days));
        } else {
            return BigDecimal.valueOf(300);
        }
    }
}
