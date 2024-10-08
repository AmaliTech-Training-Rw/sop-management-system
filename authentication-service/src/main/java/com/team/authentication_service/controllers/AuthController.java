package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.LoginRequestDto;
import com.team.authentication_service.dtos.LoginResponseDto;
import com.team.authentication_service.dtos.RegisterRequestDto;
import com.team.authentication_service.dtos.ValidateTokenResponseDto;
import com.team.authentication_service.models.User;
import com.team.authentication_service.services.AuthService;
import com.team.authentication_service.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            User user = authService.login(loginRequestDto);
            String token = jwtUtil.generateToken(user);
            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                    .token(token).
                    expiresIn(jwtUtil.getJwtExpirationTime())
                    .build();
            return ResponseEntity.ok(loginResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto requestDto) throws Exception {
        try {
            authService.register(requestDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Error registering user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public String resetPassword() {
        return "Reset password";
    }

    @PostMapping("/create-password")
    public String createPassword() {
        return "Create password";
    }

    @GetMapping("/get-user_details")
    public String getUserDetails() {
        return "get user details";
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestParam String token) throws Exception {
        try {
            ValidateTokenResponseDto responseDto = ValidateTokenResponseDto.builder()
                    .email(
                            authService.validateToken(token)
                    )
                    .build();
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
