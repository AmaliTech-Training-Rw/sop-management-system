package com.team.authentication_service.services;

import com.team.authentication_service.dtos.LoginRequestDto;
import com.team.authentication_service.dtos.RegisterRequestDto;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequestDto registerRequestDto) throws Exception {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        User user = User
                .builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .position(registerRequestDto.getPosition())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public User login(LoginRequestDto loginRequestDto) throws Exception {
        System.out.println(loginRequestDto.getEmail());
        System.out.println(loginRequestDto.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
            );
            System.out.println("Authentication Successful");
            return userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    public String validateToken(String token) throws Exception {
        try {
            return jwtUtil.extractClaim(token, Claims::getSubject);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }
}
