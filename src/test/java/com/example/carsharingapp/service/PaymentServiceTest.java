package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.mapper.payment.PaymentMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.bot.TelegramNotificationService;
import com.example.carsharingapp.service.payment.impl.PaymentServiceImpl;
import com.example.carsharingapp.service.stripe.StripeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private TelegramNotificationService telegramNotificationService;

    @Test
    @DisplayName("""
            Create a new payment
            """)
    public void createPayment_WithValidRequest_ReturnPaymentResponseDto() {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto()
                .setRentalId(1L)
                .setType(PaymentType.PAYMENT);

        Car car = new Car()
                .setBrand("Audi")
                .setModel("A5")
                .setDailyFee(BigDecimal.valueOf(700))
                .setType(Type.SEDAN)
                .setInventory(50);

        User user = new User()
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setDeleted(false);

        Rental rental = new Rental()
                .setId(1L)
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCar(car)
                .setUser(user)
                .setActualReturnDate(null);

        //Session session = new Session().se;

        BigDecimal totalAmount = BigDecimal.valueOf(100);

        when(rentalRepository.findById(paymentRequestDto.getRentalId())).thenReturn(Optional.of(rental));
        when(stripeService.createRentalPaymentSession(rental, totalAmount)).thenReturn();
    }
}
