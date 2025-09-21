package com.example.carsharingapp.controller.auth;

import com.example.carsharingapp.dto.user.UserLoginRequestDto;
import com.example.carsharingapp.dto.user.UserLoginResponseDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
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
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("""
            Register a new user
            """)
    @Sql(scripts = {
            "classpath:database/controller/auth/truncate/truncate-users-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void registerNewUser_WithValidRequestBody_ReturnUserResponseDto() throws Exception {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto()
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setEmail("nikolya.cr@gmail.com")
                .setPassword("12345678")
                .setRepeatPassword("12345678");

        UserResponseDto expected = new UserResponseDto()
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setEmail("nikolya.cr@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);

        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @DisplayName("""
            Login user
            """)
    @Sql(scripts = {
            "classpath:database/controller/auth/add/add-user-to-users-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/controller/auth/truncate/truncate-users-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void loginUser_WithValidCredentials_ReturnUserLoginResponseDto() throws Exception {
        UserLoginRequestDto userloginRequestDto = new UserLoginRequestDto()
                .setEmail("nikolya.cr@gmail.com")
                .setPassword("12345678");

        String jsonRequest = objectMapper.writeValueAsString(userloginRequestDto);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserLoginResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserLoginResponseDto.class);
        assertNotNull(actual.token());
    }
}
