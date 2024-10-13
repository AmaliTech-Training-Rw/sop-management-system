package com.team.email_service.controllers;

import com.team.email_service.dtos.SendEmailRequestDto;
import com.team.email_service.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email-service")
public class EmailController {
    private EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody SendEmailRequestDto emailRequestDto) {
        try {
            emailService.sendEmail(emailRequestDto);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("Error sending email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
