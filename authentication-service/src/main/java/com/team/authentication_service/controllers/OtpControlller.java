package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.GenerateOtpEmail;
import com.team.authentication_service.dtos.OtpReqDto;
import com.team.authentication_service.models.Otp;
import com.team.authentication_service.services.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class OtpControlller {
    private final OtpService otpService;

    public OtpControlller(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@Valid @RequestBody GenerateOtpEmail email) throws Exception {
        try {
            otpService.generateOtp(email.getEmail());
            return ResponseEntity.ok("OTP generated. Check you email.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> compare(@Valid @RequestBody OtpReqDto otpReqDto) throws Exception {
        try {
            otpService.validateOtp(otpReqDto.getOtp());
            return ResponseEntity.ok("OTP verified.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
