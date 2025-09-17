package com.example.carsharingapp.repository.user;

import com.example.carsharingapp.model.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("""
            Find User by email
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findUser_ByEmail_ReturnOptionalOfUser() {
        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("$2a$10$LLugUeKY.RQnC988wXT2zebftBRFtH0s7uucFS3Mq.t1zhbgQUMYi")
                .setDeleted(false);

        Optional<User> expected = Optional.of(user);
        Optional<User> actual = userRepository.findByEmail(user.getEmail());

        assertTrue(EqualsBuilder.reflectionEquals(expected.get(), actual.get()));
    }

    @Test
    @DisplayName("""
            Check if user exist by email
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void checkIfUserExist_ByEmail_ReturnTrue() {
        User user = new User()
                .setId(1L)
                .setEmail("nikolya.cr@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("$2a$10$LLugUeKY.RQnC988wXT2zebftBRFtH0s7uucFS3Mq.t1zhbgQUMYi")
                .setDeleted(false);

        boolean expected = true;
        boolean actual = userRepository.existsByEmail(user.getEmail());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Check if user exist by email
            """)
    @Sql(scripts = {
            "classpath:database/repository/user/add/add-user-to-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/repository/user/truncate/truncate-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void checkIfUserExist_ByEmail_ReturnFalse() {
        User user = new User()
                .setId(1L)
                .setEmail("nikolya@gmail.com")
                .setFirstName("Mykola")
                .setLastName("Kovbasiuk")
                .setPassword("$2a$10$LLugUeKY.RQnC988wXT2zebftBRFtH0s7uucFS3Mq.t1zhbgQUMYi")
                .setDeleted(false);

        boolean expected = false;
        boolean actual = userRepository.existsByEmail(user.getEmail());

        assertEquals(expected, actual);
    }
}
