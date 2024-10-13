package com.team.authentication_service.dtos;

import com.team.authentication_service.enums.Position;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto implements Serializable {
    private String name;
    private String email;
    private Position position;
    private int department;
    private Role[] roles;
}
