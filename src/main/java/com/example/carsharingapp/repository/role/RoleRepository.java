package com.example.carsharingapp.repository.role;

import com.example.carsharingapp.model.role.Role;
import com.example.carsharingapp.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
