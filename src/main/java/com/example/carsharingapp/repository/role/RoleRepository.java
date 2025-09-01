package com.example.carsharingapp.repository.role;

import com.example.carsharingapp.model.enums.RoleName;
import com.example.carsharingapp.model.role.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
