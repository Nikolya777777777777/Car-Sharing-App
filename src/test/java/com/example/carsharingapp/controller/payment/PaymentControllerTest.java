package com.example.carsharingapp.controller.payment;

import com.example.carsharingapp.dto.payment.PaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("""
            Create a new payment
            """)
    @Sql(scripts = {
            "classpath:database/controller/payment/add/add-user-to-users-table.sql",
            "classpath:database/controller/payment/add/add-car-to-cars-table.sql",
            "classpath:database/controller/payment/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/payment/truncate/truncate-payment-table.sql",
            "classpath:database/controller/payment/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/payment/truncate/truncate-cars-table.sql",
            "classpath:database/controller/payment/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createNewPayment_WithValidRequestBody_ReturnPaymentResponseDto() throws Exception {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto()
                .setType(PaymentType.PAYMENT)
                .setRentalId(1L);

        PaymentResponseDto expected = new PaymentResponseDto()
                .setId(1L)
                .setAmountToPay(new BigDecimal("700.00"))
                .setType(PaymentType.PAYMENT)
                .setSessionId("testSession")
                .setSessionUrl("testSessionUrl")
                .setStatus(Status.PENDING);

        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);

        MvcResult result = mockMvc.perform(post("/payment")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PaymentResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentResponseDto.class);
        assertNotNull(actual.getSessionId());
        assertNotNull(actual.getSessionUrl());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "sessionId", "sessionUrl"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("""
            Get payment by user id
            """)
    @Sql(scripts = {
            "classpath:database/controller/payment/add/add-user-to-users-table.sql",
            "classpath:database/controller/payment/add/add-car-to-cars-table.sql",
            "classpath:database/controller/payment/add/add-two-rentals-to-rentals-table.sql",
            "classpath:database/controller/payment/add/add-two-payments-to-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/payment/truncate/truncate-payment-table.sql",
            "classpath:database/controller/payment/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/payment/truncate/truncate-cars-table.sql",
            "classpath:database/controller/payment/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPayment_ByUserId_ReturnPageOfAllUsersPaymentResponseDtos() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto()
                .setId(1L)
                .setAmountToPay(new BigDecimal("700.00"))
                .setType(PaymentType.PAYMENT)
                .setSessionId("testSession")
                .setSessionUrl("testSessionUrl")
                .setStatus(Status.PENDING);

        Page<PaymentResponseDto> expected = new PageImpl<>(List.of(paymentResponseDto), pageable, 1);

        MvcResult result = mockMvc.perform(get("/payment/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, PaymentResponseDto.class);

        PageResponse<PaymentResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );

        assertNotNull(actual.content.get(0).getSessionId());
        assertNotNull(actual.content.get(0).getSessionUrl());
        assertEquals(expected.getTotalElements(), actual.totalElements);
        assertEquals(expected.getContent().get(0).getAmountToPay().setScale(2), actual.content.get(0).getAmountToPay().setScale(2));
        assertTrue(EqualsBuilder.reflectionEquals(expected.getContent().get(0), actual.content.get(0), "sessionId", "sessionUrl", "amountToPay"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("""
            Get success payment status
            """)
    @Sql(scripts = {
            "classpath:database/controller/payment/add/add-user-to-users-table.sql",
            "classpath:database/controller/payment/add/add-car-to-cars-table.sql",
            "classpath:database/controller/payment/add/add-two-rentals-to-rentals-table.sql",
            "classpath:database/controller/payment/add/add-two-payments-to-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/payment/truncate/truncate-payment-table.sql",
            "classpath:database/controller/payment/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/payment/truncate/truncate-cars-table.sql",
            "classpath:database/controller/payment/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getSuccessPayment_BySessionId_ReturnPaymentStatusResponseDto() throws Exception {
        PaymentStatusResponseDto expected = new PaymentStatusResponseDto()
                .setStatus(Status.PAID);

        MvcResult result = mockMvc.perform(get("/payment/success")
                        .content("sessionId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentStatusResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), PaymentStatusResponseDto.class);

        assertEquals(expected.getStatus(), actual.getStatus());
    }

    public static class PageResponse<T> {
        public List<T> content;
        public long totalElements;
    }
}
