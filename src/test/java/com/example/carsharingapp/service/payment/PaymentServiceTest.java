package com.example.carsharingapp.service.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCar(car)
                .setUser(user)
                .setActualReturnDate(null);

        Session session = new Session();
        session.setId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1TOL76MdGnmrhJFkb2iWRff"
                + "UQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlw"
                + "VWo9YlRjcTVTYjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249S"
                + "kJhZjVuUWJoXGh8Nkg3VkRQVU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8"
                + "dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl");

        Payment payment = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(rental)
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        when(rentalRepository.findById(paymentRequestDto.getRentalId())).thenReturn(Optional.of(rental));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(stripeService.createRentalPaymentSession(any(Rental.class), any(BigDecimal.class))).thenReturn(session);
        when(paymentMapper.toResponseDto(any(Payment.class))).thenReturn(paymentResponseDto);

        PaymentResponseDto result = paymentService.create(paymentRequestDto);
        assertTrue(EqualsBuilder.reflectionEquals(result, paymentResponseDto));
        verify(rentalRepository).findById(paymentRequestDto.getRentalId());
        verify(paymentRepository).save(any(Payment.class));
        verify(telegramNotificationService).sendNotification(anyString());
        verify(paymentMapper).toResponseDto(any(Payment.class));
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Create a new payment with invalid id
            """)
    public void createPayment_WithInValidId_ShouldThrowException() {
        Long rentalId = 1L;

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto()
                .setRentalId(1L)
                .setType(PaymentType.PAYMENT);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.create(paymentRequestDto));
        assertThat(exception.getMessage()).isEqualTo("Rental was not found with id: " + rentalId);
        verify(rentalRepository).findById(paymentRequestDto.getRentalId());
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Get payment status
            """)
    public void getPaymentStatus_BySessionId_ReturnPaymentResponseDto() {
        String PAID = "paid";

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
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCar(car)
                .setUser(user)
                .setActualReturnDate(null);

        Session session = new Session();
        session.setId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1TOL76MdGnmrhJFkb2iWRff"
                + "UQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlw"
                + "VWo9YlRjcTVTYjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249S"
                + "kJhZjVuUWJoXGh8Nkg3VkRQVU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8"
                + "dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl");

        Payment payment = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(rental)
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        PaymentStatusResponseDto responseDto = new PaymentStatusResponseDto()
                .setStatus(Status.PAID);

        when(paymentRepository.findBySessionId(session.getId())).thenReturn(Optional.ofNullable(payment));
        when(stripeService.checkPaymentStatus(session.getId())).thenReturn(PAID);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentStatusResponseDto result = paymentService.getPaymentStatus(session.getId());

        assertTrue(EqualsBuilder.reflectionEquals(result, responseDto));
        verify(paymentRepository).findBySessionId(session.getId());
        verify(stripeService).checkPaymentStatus(session.getId());
        verify(paymentRepository).save(any(Payment.class));
        verify(telegramNotificationService).sendNotification(anyString());
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper, telegramNotificationService);
    }

    @Test
    @DisplayName("""
            Get payment by user id
            """)
    public void getPayment_ByUserId_ReturnPageOfPaymentResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
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
                .setRentalDate(LocalDateTime.now())
                .setReturnDate(LocalDateTime.now().plusDays(1))
                .setCar(car)
                .setUser(user)
                .setActualReturnDate(null);

        Session session = new Session();
        session.setId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1TOL76MdGnmrhJFkb2iWRff"
                + "UQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlw"
                + "VWo9YlRjcTVTYjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249S"
                + "kJhZjVuUWJoXGh8Nkg3VkRQVU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8"
                + "dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl");

        Payment payment = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(rental)
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        PaymentResponseDto responseDto = new PaymentResponseDto()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PENDING);

        Page<PaymentResponseDto> expected = new PageImpl<>(List.of(responseDto),  pageable, 1);
        Page<Payment> pageOfPayment = new PageImpl<>(List.of(payment),  pageable, 1);

        when(paymentRepository.findByUserId(user.getId(), pageable)).thenReturn(pageOfPayment);
        when(paymentMapper.toResponseDto(payment)).thenReturn(responseDto);

        Page<PaymentResponseDto> result = paymentService.getPaymentsByUserId(user.getId(), pageable);

        assertTrue(EqualsBuilder.reflectionEquals(result, expected));
        verify(paymentRepository).findByUserId(user.getId(), pageable);
        verify(paymentMapper).toResponseDto(payment);
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper, telegramNotificationService);
    }
}
