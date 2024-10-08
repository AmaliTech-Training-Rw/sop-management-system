package com.team.authentication_service.dtos;

import com.team.authentication_service.enums.Position;
import com.team.authentication_service.models.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private Position position;
    private Department department;
    private boolean isVerified = false;
    private String[] roles;
}
