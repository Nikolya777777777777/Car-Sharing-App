package com.example.carsharingapp.service;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.mapper.payment.PaymentMapper;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.example.carsharingapp.model.enums.Type;
import com.example.carsharingapp.model.payment.Payment;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.repository.payment.PaymentRepository;
import com.example.carsharingapp.repository.rental.RentalRepository;
import com.example.carsharingapp.service.bot.TelegramNotificationService;
import com.example.carsharingapp.service.payment.impl.PaymentServiceImpl;
import com.example.carsharingapp.service.stripe.StripeService;
import org.apache.commons.lang3.builder.EqualsBuilder;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

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

        Payment payment = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(rental)
                .setSessionId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0")
                .setSessionUrl("https://checkout.stripe.com/c/pay/cs_test_a1TOL76MdGnmrhJFkb2iWRff"
                        + "UQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlw"
                        + "VWo9YlRjcTVTYjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249S"
                        + "kJhZjVuUWJoXGh8Nkg3VkRQVU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8"
                        + "dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl")
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setSessionId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0")
                .setSessionUrl("https://checkout.stripe.com/c/pay/cs_test_a1TOL76MdGnmrhJFkb2iWRff"
                        + "UQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlw"
                        + "VWo9YlRjcTVTYjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249S"
                        + "kJhZjVuUWJoXGh8Nkg3VkRQVU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8"
                        + "dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl")
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        BigDecimal totalAmount = BigDecimal.valueOf(100);

        Session session = stripeService.createRentalPaymentSession(rental, totalAmount);

        when(rentalRepository.findById(paymentRequestDto.getRentalId())).thenReturn(Optional.of(rental));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(stripeService.createRentalPaymentSession(rental, totalAmount)).thenReturn(session);
        when(paymentMapper.toResponseDto(payment)).thenReturn(paymentResponseDto);

        PaymentResponseDto result = paymentService.create(paymentRequestDto);
        assertTrue(EqualsBuilder.reflectionEquals(result, paymentResponseDto));
        verify(rentalRepository).findById(paymentRequestDto.getRentalId());
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toResponseDto(payment);
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper);
    }
}
