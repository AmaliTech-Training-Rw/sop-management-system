package com.team.authentication_service.dtos;

import com.team.authentication_service.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedUserHeaders {
    private String email;
    private List<String> roles;
    private String position;
}
