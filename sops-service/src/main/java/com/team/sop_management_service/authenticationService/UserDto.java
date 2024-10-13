package com.team.sop_management_service.authenticationService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String name;
    private String email;
    private String position;
    private DepartmentDto department;
    private boolean isVerified = false;
    private List<String> roles;
}