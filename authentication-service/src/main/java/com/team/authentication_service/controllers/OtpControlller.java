package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.GenerateOtpEmail;
import com.team.authentication_service.dtos.OtpReqDto;
import com.team.authentication_service.dtos.ResponseDto;
import com.team.authentication_service.models.Otp;
import com.team.authentication_service.services.OtpService;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
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
    public ResponseEntity<Object> generateOtp(@Valid @RequestBody GenerateOtpEmail email) throws Exception {
        try {
//          Generate an otp and send it to the user's email
            otpService.generateOtp(email.getEmail());
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("OTP generated. Check you email.")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support.")
                                    .data(null)
                                    .build()
                    );
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Object> compare(@Valid @RequestBody OtpReqDto otpReqDto) throws Exception {
        try {
//          Validate the otp
            otpService.validateOtp(otpReqDto.getOtp());
            return ResponseEntity.badRequest()
                    .body(
                            ResponseDto.builder()
                                    .message("OTP verified.")
                                    .data(null)
                                    .build()
                    );
        } catch (ReturnableException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support.")
                                    .data(null)
                                    .build()
                    );
        }
    }
}
