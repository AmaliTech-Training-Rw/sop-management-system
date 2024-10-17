package com.team.authentication_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.authentication_service.dtos.*;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.services.AuthService;
import com.team.authentication_service.services.UserRoleService;
import com.team.authentication_service.utils.JwtUtil;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserRoleService userRoleService;

    public AuthController(JwtUtil jwtUtil, AuthService authService, UserRoleService userRoleService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userRoleService = userRoleService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
//          call the login method in the auth service
            User user = authService.login(loginRequestDto);

//          get user roles and create  a list out of them
            List<UserRole> userRoles = userRoleService.getAllUserRoles(user.getId());
            List<Role> roles = userRoles.stream().map(UserRole::getRole).toList();

//          add all the roles into the jwt token
            Map<String, Object> extraClaims = new HashMap<String, Object>();
            extraClaims.put("roles", roles);
            String token = jwtUtil.generateToken(extraClaims, user);

//          create a login reponse transfer object and return
            LoginResponseDto loginResponseDto = LoginResponseDto
                    .builder()
                    .token(token)
                    .expiresIn(
                            jwtUtil.getJwtExpirationTime()
                    ).build();

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(loginResponseDto)
                            .build());
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode()).body(
                    ErrorResponse.builder()
                            .status("Error")
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder().status("Error")
                            .message("Internal server error. Please contact system admins")
                            .build()
                    );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterRequestDto requestDto,
            @RequestHeader("x-auth-user-email") String userHeadersJson
    ) throws Exception {
//      get and parse the user header from json
        ObjectMapper objectMapper = new ObjectMapper();
        AuthenticatedUserHeaders adminHeaders = objectMapper.readValue(
                userHeadersJson,
                AuthenticatedUserHeaders.class
        );

        try {
//          Check whether user has the rights to register another user
//          The user should be an admin for them to register a new user
            if (!adminHeaders.getPosition().equals("SUPER_ADMIN"))
                throw new ReturnableException(
                        "You're no the super admin. Contact the admin to get registered.",
                        HttpStatus.FORBIDDEN
                );

//          if the user has the rights register the new user
            authService.register(requestDto);

//          return response to user
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("User registered successfully. Visit your email to set it up.")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode()).body(
                    ErrorResponse.builder()
                            .status("Error")
                            .message(
                                    e.getMessage()
                            ).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ErrorResponse
                                    .builder()
                                    .status("Error")
                                    .message("Internal server error. Please contact system admins")
                                    .build()
                    );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody CreatePasswordReqDto passwordReqDto) {
        try {
//          Call the reset password service and give it the request and then return a response
            authService.resetPassword(passwordReqDto);


            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Password reset successfully")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message(e.getMessage())
                                    .build()
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message("Internal server error. Please contact system admins")
                                    .build());
        }
    }

    @PostMapping("/create-password")
    public ResponseEntity<Object> createPassword(@Valid @RequestBody CreatePasswordReqDto passwordReqDto) throws Exception {
        try {
//            Create password controller.
//            We should be comparing the email in the request headers to the email passed through the body
//            or only rely on the email within the auth headers. For the user to not reset other people's accounts
            authService.createPassword(passwordReqDto);
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Password created successfully")
                            .data(null)
                            .build());
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message(e.getMessage())
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message("Internal server error. Please contact system admins")
                                    .build());
        }

    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestParam String token) throws Exception {
        try {
            ValidateTokenResponseDto responseDto = ValidateTokenResponseDto.builder()
                    .headers(authService.validateToken(token))
                    .build();
            return ResponseEntity.ok(responseDto);
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message(e.getMessage())
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ErrorResponse.builder()
                                    .status("Error")
                                    .message("Internal server error. Please contact system admins")
                                    .build()
                    );
        }
    }
}