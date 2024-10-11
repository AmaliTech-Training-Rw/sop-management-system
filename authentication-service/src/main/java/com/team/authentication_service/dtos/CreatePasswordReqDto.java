package com.team.authentication_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePasswordReqDto {
    private String email;
    private String password;
    private String confirmPassword;
}
