package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.example.carsharingapp.model.payment.Payment;
import com.example.carsharingapp.model.rental.Rental;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {
    @Autowired
    PaymentRepository paymentRepository;

    @Test
    @DisplayName("""
            Find payment by session id
            """)
    @Sql(scripts = {
            "classpath:database/repository/payment/add/add-two-payments-to-payments-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/payment/truncate/truncate-payments-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findPayment_BySessionId_ReturnOptionalOfPayment() {
        Session session = new Session();
        session.setId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1IKNt0COMCX9m9aawNPOGRP10lF"
                + "H9SvESORSmN1mRD8n5duuWBfw8mNX0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlwVWo9YlRjcTVT"
                + "Yjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249SkJhZjVuUWJoXGh8Nkg3VkRQ"
                + "VU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVp"
                + "ZGZgbWppYWB3dic%2FcXdwYHgl");

        Payment expected = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(new Rental().setId(1L))
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PAID);

        Optional<Payment> actual =  paymentRepository.findBySessionId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        assertEquals(expected.getSessionId(), actual.get().getSessionId());
        assertEquals(expected.getSessionUrl(), actual.get().getSessionUrl());
        assertEquals(expected.getType(), actual.get().getType());
        assertEquals(expected.getAmountToPay(), actual.get().getAmountToPay());
        assertEquals(expected.getRental().getId(), actual.get().getRental().getId());
        assertEquals(expected.getStatus(), actual.get().getStatus());
        assertEquals(expected.getId(), actual.get().getId());
        assertFalse(actual.get().isDeleted());
    }

    @Test
    @DisplayName("""
            Find payment by user id
            """)
    @Sql(scripts = {
            "classpath:database/repository/payment/add/add-two-payments-to-payments-table.sql",
            "classpath:database/repository/payment/add/add-user-to-users-table.sql",
            "classpath:database/repository/payment/add/add-car-to-cars-table.sql",
            "classpath:database/repository/payment/add/add-rental-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/payment/truncate/truncate-cars-table.sql",
            "classpath:database/repository/payment/truncate/truncate-payments-table.sql",
            "classpath:database/repository/payment/truncate/truncate-users-table.sql",
            "classpath:database/repository/payment/truncate/truncate-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findPayment_ByUserId_ReturnOptionalOfPayment() {
        Pageable pageable = PageRequest.of(0, 10);
        Session session = new Session();
        session.setId("cs_test_a1TOL76MdGnmrhJFkb2iWRffUQ0axHeXWvsTb4HaSUhfwNk93wuaknsBo0");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1IKNt0COMCX9m9aawNPOGRP10lF"
                + "H9SvESORSmN1mRD8n5duuWBfw8mNX0#fidkdWxOYHwnPyd1blpxYHZxWjA0VjVRdzdDSGlwVWo9YlRjcTVT"
                + "Yjc3UnJSPTJSVVw2cD1pTVdwXHd9M11QSWtTV1RpZjFAPUpcU29CamBBYTVMd249SkJhZjVuUWJoXGh8Nkg3VkRQ"
                + "VU9MNTVXVlw0NmI0PCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVp"
                + "ZGZgbWppYWB3dic%2FcXdwYHgl");

        Payment expected = new Payment()
                .setId(1L)
                .setAmountToPay(BigDecimal.valueOf(700))
                .setRental(new Rental().setId(1L))
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setType(PaymentType.PAYMENT)
                .setStatus(Status.PAID);

        Page<Payment> actual =  paymentRepository.findByUserId(1L, pageable);
        assertEquals(expected.getSessionId(), actual.getContent().get(0).getSessionId());
        assertEquals(expected.getSessionUrl(), actual.getContent().get(0).getSessionUrl());
        assertEquals(expected.getType(), actual.getContent().get(0).getType());
        assertEquals(expected.getAmountToPay(), actual.getContent().get(0).getAmountToPay());
        assertEquals(expected.getRental().getId(), actual.getContent().get(0).getRental().getId());
        assertEquals(expected.getStatus(), actual.getContent().get(0).getStatus());
        assertEquals(expected.getId(), actual.getContent().get(0).getId());
        assertFalse(actual.getContent().get(0).isDeleted());
    }
}
