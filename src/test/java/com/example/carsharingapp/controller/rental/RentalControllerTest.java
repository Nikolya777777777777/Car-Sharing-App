package com.example.carsharingapp.controller.rental;

import com.example.carsharingapp.controller.payment.PaymentControllerTest;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.rental.*;
import com.example.carsharingapp.model.rental.Rental;
import com.example.carsharingapp.service.bot.TelegramNotificationService;
import com.example.carsharingapp.service.payment.PaymentService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.glassfish.grizzly.http.util.MimeType.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    @MockitoBean
    private TelegramNotificationService telegramNotificationService;
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
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("""
            Create a new rental
            """)
    @Sql(scripts = {
            "classpath:database/controller/rental/add/add-user-to-users-table.sql",
            "classpath:database/controller/rental/add/add-car-to-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/rental/truncate/truncate-cars-table.sql",
            "classpath:database/controller/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createNewRental_WithValidRequest_ReturnRentalResponseDtoWithoutActualReturnDate() throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto()
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .setCarId(1L);

        RentalResponseDtoWithoutActualReturnDate expected = new RentalResponseDtoWithoutActualReturnDate()
                .setId(1L)
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .setCarId(1L);

        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDtoWithoutActualReturnDate actual =  objectMapper.readValue(result.getResponse().getContentAsString(), RentalResponseDtoWithoutActualReturnDate.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Return rental
            """)
    @Sql(scripts = {
            "classpath:database/controller/rental/add/add-user-to-users-table.sql",
            "classpath:database/controller/rental/add/add-car-to-cars-table.sql",
            "classpath:database/controller/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/rental/truncate/truncate-cars-table.sql",
            "classpath:database/controller/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void returnRental_WithValidRequest_ReturnPageOfRentalResponseDto() throws Exception {
        RentalReturnDto rentalReturnDto = new RentalReturnDto()
                .setCarIds(List.of(1L));

        RentalResponseDto expected = new RentalResponseDto()
                .setId(2L)
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 0, 0, 0))
                .setActualReturnDate(LocalDateTime.of(2025, 8, 26, 1, 0, 0))
                .setCarId(1L);

        String jsonRequest = objectMapper.writeValueAsString(rentalReturnDto);

        MvcResult result = mockMvc.perform(post("/rentals/return")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, RentalResponseDto.class);

        PageResponse<RentalResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.content.get(0), "actualReturnDate"));
        verify(telegramNotificationService).sendNotification(anyString());
    }

    @Test
    @DisplayName("""
            Get all active rental
            """)
    @Sql(scripts = {
            "classpath:database/controller/rental/add/add-user-to-users-table.sql",
            "classpath:database/controller/rental/add/add-car-to-cars-table.sql",
            "classpath:database/controller/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/rental/truncate/truncate-cars-table.sql",
            "classpath:database/controller/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void getAllActiveRental_WithValidRequest_ReturnPageOfRentalDeciderDto() throws Exception {
        RentalActiveOrNotActiveRequestDto activeOrNotRequestDto = new RentalActiveOrNotActiveRequestDto()
                .setActive(true);

        RentalResponseDtoWithoutActualReturnDate expected = new RentalResponseDtoWithoutActualReturnDate()
                .setId(2L)
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 0, 0, 0))
                .setCarId(1L);

        String jsonRequest = objectMapper.writeValueAsString(activeOrNotRequestDto);

        MvcResult result = mockMvc.perform(get("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, RentalResponseDtoWithoutActualReturnDate.class);

        PageResponse<RentalResponseDtoWithoutActualReturnDate> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.content.get(0), "actualReturnDate"));
    }

    @Test
    @DisplayName("""
            Get all not active rental
            """)
    @Sql(scripts = {
            "classpath:database/controller/rental/add/add-user-to-users-table.sql",
            "classpath:database/controller/rental/add/add-car-to-cars-table.sql",
            "classpath:database/controller/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/rental/truncate/truncate-cars-table.sql",
            "classpath:database/controller/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void getAllNotActiveRental_WithValidRequest_ReturnPageOfRentalDeciderDto() throws Exception {
        RentalActiveOrNotActiveRequestDto activeOrNotRequestDto = new RentalActiveOrNotActiveRequestDto()
                .setActive(false);

        RentalResponseDto expected = new RentalResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 0, 0, 0))
                .setActualReturnDate(LocalDateTime.of(2025, 8, 26, 1, 0, 0))
                .setCarId(1L);

        String jsonRequest = objectMapper.writeValueAsString(activeOrNotRequestDto);

        MvcResult result = mockMvc.perform(get("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, RentalResponseDto.class);

        PageResponse<RentalResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.content.get(0)));
    }

    @Test
    @DisplayName("""
            Get rental by user's id and rental's id
            """)
    @Sql(scripts = {
            "classpath:database/controller/rental/add/add-user-to-users-table.sql",
            "classpath:database/controller/rental/add/add-car-to-cars-table.sql",
            "classpath:database/controller/rental/add/add-two-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/rental/truncate/truncate-rentals-table.sql",
            "classpath:database/controller/rental/truncate/truncate-cars-table.sql",
            "classpath:database/controller/rental/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void getAllRentals_ByUserIdAndRentalId_ReturnRentalDeciderDto() throws Exception {
        RentalResponseDto expected = new RentalResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setRentalDate(LocalDateTime.of(2025, 8, 25, 0, 0, 0))
                .setReturnDate(LocalDateTime.of(2025, 8, 26, 0, 0, 0))
                .setActualReturnDate(LocalDateTime.of(2025, 8, 26, 1, 0, 0))
                .setCarId(1L);

        MvcResult result = mockMvc.perform(get("/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), RentalResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    public static class PageResponse<T> {
        public List<T> content;
        public long totalElements;
    }
}
