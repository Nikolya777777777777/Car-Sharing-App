package com.example.carsharingapp.dto.user;

import com.example.carsharingapp.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain=true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateRoleRequestDto {
    private RoleName role;
}
