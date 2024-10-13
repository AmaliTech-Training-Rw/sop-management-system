package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.*;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.services.AuthService;
import com.team.authentication_service.services.UserRoleService;
import com.team.authentication_service.utils.JwtUtil;
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
            System.out.println(loginRequestDto);

            User user = authService.login(loginRequestDto);

            System.out.println(user);

            List<UserRole> userRoles = userRoleService.getAllUserRoles(user.getId());

            Map<String, Object> extraClaims = new HashMap<String, Object>();
            List<Role> roles = userRoles.stream().map(UserRole::getRole).toList();

            System.out.println(roles);

            extraClaims.put("roles", roles);

            String token = jwtUtil.generateToken(extraClaims, user);

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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User registered successfully. Visit your email to set it up.", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody CreatePasswordReqDto passwordReqDto) {
        try {
            authService.resetPassword(passwordReqDto);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-password")
    public ResponseEntity<String> createPassword(@Valid @RequestBody CreatePasswordReqDto passwordReqDto) throws Exception {
//        @RequestHeader(value = "x-auth-user-email", required = false)
        try {
            authService.createPassword(passwordReqDto);
            return ResponseEntity.ok("Password created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/get-user_details")
    public String getUserDetails() {
        return "get user details";
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestParam String token) throws Exception {
        try {
            System.out.println("Validating #################");
            System.out.println("token: " + token);
            System.out.println("Validating #################");
            ValidateTokenResponseDto responseDto = ValidateTokenResponseDto.builder().headers(
                    authService.validateToken(token)).build();
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}