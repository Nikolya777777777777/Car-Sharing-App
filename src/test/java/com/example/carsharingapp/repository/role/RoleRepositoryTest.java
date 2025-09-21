package com.example.carsharingapp.repository.role;

import com.example.carsharingapp.model.enums.RoleName;
import com.example.carsharingapp.model.role.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("""
            Find role by name
            """)
    public void findRole_ByName_ReturnOptionalOfRole() {
        Role role = new  Role()
                .setId(1L)
                .setName(RoleName.ROLE_CUSTOMER);

        Optional<Role> actual = roleRepository.findByName(RoleName.ROLE_CUSTOMER);
        Optional<Role> expected = Optional.of(role);

        assertTrue(actual.isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(actual.get(), expected.get()));
    }
}
