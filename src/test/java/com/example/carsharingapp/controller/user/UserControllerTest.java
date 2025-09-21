package com.example.carsharingapp.controller.user;

import com.example.carsharingapp.dto.user.UpdateRoleRequestDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.model.enums.RoleName;
import com.example.carsharingapp.model.role.Role;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.repository.user.UserRepository;
import com.example.carsharingapp.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("""
            Get all details about user
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql",
            "classpath:database/repository/user/add/add-user-role-to-users-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-roles-table.sql",
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void getAllInformationAboutUser_WithValidRequest_ReturnUserResponseDto() throws Exception {
        UserResponseDto expected = new UserResponseDto()
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setEmail("nikolya.cr@gmail.com");

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual =  objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(actual, expected));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("""
            Update user's role
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql",
            "classpath:database/repository/user/add/add-user-role-to-users-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-roles-table.sql",
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserRole_WithValidRequest_ReturnUserResponseDto() throws Exception {
        UpdateRoleRequestDto requestDto = new UpdateRoleRequestDto()
                .setRole(RoleName.ROLE_MANAGER);

        UserResponseDto expected = new UserResponseDto()
                .setId(1L)
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setEmail("nikolya.cr@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/1/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual =  objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(actual, expected));

        User user = userRepository.findByIdWithRoles(1L).orElseThrow();
        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        assertTrue(roleNames.contains(RoleName.ROLE_MANAGER));
    }

    @Test
    @DisplayName("""
            Update info about user
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql",
            "classpath:database/repository/user/add/add-user-role-to-users-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-roles-table.sql",
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "nikolya.cr@gmail.com")
    public void updateInfoAboutUser_WithValidRequest_ReturnUserResponseDto() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
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

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual =  objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(actual, expected));
    }
}
