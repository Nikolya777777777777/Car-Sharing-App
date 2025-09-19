package com.example.carsharingapp.controller.car;

import com.example.carsharingapp.dto.car.CarRequestDto;
import com.example.carsharingapp.dto.car.CarResponseDto;
import com.example.carsharingapp.model.enums.Type;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
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
            Create a new user
            """)
    @Sql(scripts = {
            "classpath:database/controller/car/truncate/truncate-cars-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createNewCar_WithValidRequestBody_ReturnCarResponseDto() throws Exception {
        CarRequestDto carRequestDto = new CarRequestDto()
                .setBrand("Audi")
                .setModel("A5")
                .setInventory(50)
                .setType(Type.SEDAN)
                .setDailyFee(BigDecimal.valueOf(700));

        CarResponseDto expected = new CarResponseDto()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setInventory(50)
                .setType(Type.SEDAN)
                .setDailyFee(BigDecimal.valueOf(700));
        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);

        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CarResponseDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("""
            Get all cars
            """)
    @Sql(scripts = {
            "classpath:database/controller/car/add/add-two-cars-to-cars-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/car/truncate/truncate-cars-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllCars_WithPageable_ReturnPageOfCarResponseDto() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);

        CarResponseDto carResponseDto1 = new CarResponseDto()
                .setId(1L)
                .setBrand("Audi")
                .setModel("A5")
                .setInventory(50)
                .setType(Type.SEDAN)
                .setDailyFee(BigDecimal.valueOf(700).setScale(2));

        CarResponseDto carResponseDto2 = new CarResponseDto()
                .setId(2L)
                .setBrand("Mercedes")
                .setModel("G-class")
                .setInventory(70)
                .setType(Type.UNIVERSAL)
                .setDailyFee(BigDecimal.valueOf(800).setScale(2));

        Page<CarResponseDto> expected = new PageImpl<>(List.of(carResponseDto1, carResponseDto2), pageable, 2);
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, CarResponseDto.class);

        PageResponse<CarResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type
        );

        assertNotNull(actual);
        assertEquals(expected.getTotalElements(), actual.totalElements);
        assertTrue(EqualsBuilder.reflectionEquals(expected.getContent().get(0), actual.content.get(0)));
        assertTrue(EqualsBuilder.reflectionEquals(expected.getContent().get(1), actual.content.get(1)));
    }

    public static class PageResponse<T> {
        public List<T> content;
        public long totalElements;
    }
}
