package com.team.email_service.service;//package com.team.email_service.controller;
//
//import com.team.email_service.service.EmailService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//@RequestMapping("/api/email")
//@Api(tags = "Email Controller", description = "Endpoints for email operations")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    @Autowired
//    public EmailController(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    @PostMapping("/account-creation")
//    @ApiOperation("Send account creation email")
//    public ResponseEntity<String> sendAccountCreationEmail(@RequestParam String email) {
//        String token = emailService.generateToken();
//        CompletableFuture<Void> future = emailService.sendAccountCreationEmail(email, token);
//        future.join(); // Wait for the email to be sent
//        return ResponseEntity.ok("Account creation email sent successfully");
//    }
//
//    @PostMapping("/password-reset")
//    @ApiOperation("Send password reset email")
//    public ResponseEntity<String> sendPasswordResetEmail(@RequestParam String email) {
//        String otp = emailService.generateOtp();
//        CompletableFuture<Void> future = emailService.sendPasswordResetEmail(email, otp);
//        future.join(); // Wait for the email to be sent
//        return ResponseEntity.ok("Password reset email sent successfully");
//    }
//
//    @PostMapping("/task-notification")
//    @ApiOperation("Send task notification email")
//    public ResponseEntity<String> sendTaskNotification(@RequestParam String email, @RequestParam String taskDescription) {
//        CompletableFuture<Void> future = emailService.sendTaskNotification(email, taskDescription);
//        future.join(); // Wait for the email to be sent
//        return ResponseEntity.ok("Task notification email sent successfully");
//    }
//
//    @PostMapping("/sop-status")
//    @ApiOperation("Send SOP status update email")
//    public ResponseEntity<String> sendSopStatusNotification(@RequestParam String email, @RequestParam String sopTitle, @RequestParam boolean approved) {
//        CompletableFuture<Void> future = emailService.sendSopStatusNotification(email, sopTitle, approved);
//        future.join(); // Wait for the email to be sent
//        return ResponseEntity.ok("SOP status update email sent successfully");
//    }
//}


import com.team.email_service.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Controller", description = "Endpoints for email operations")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/account-creation")
    @Operation(summary = "Send account creation email")
    public ResponseEntity<String> sendAccountCreationEmail(@RequestParam String email) {
        String token = emailService.generateToken();
        CompletableFuture<Void> future = emailService.sendAccountCreationEmail(email, token);
        future.join(); // Wait for the email to be sent
        return ResponseEntity.ok("Account creation email sent successfully");
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestParam String email) {
        String otp = emailService.generateOtp();
        CompletableFuture<Void> future = emailService.sendPasswordResetEmail(email, otp);
        future.join(); // Wait for the email to be sent
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("/task-notification")
    @Operation(summary = "Send task notification email")
    public ResponseEntity<String> sendTaskNotification(@RequestParam String email, @RequestParam String taskDescription) {
        CompletableFuture<Void> future = emailService.sendTaskNotification(email, taskDescription);
        future.join(); // Wait for the email to be sent
        return ResponseEntity.ok("Task notification email sent successfully");
    }

    @PostMapping("/sop-status")
    @Operation(summary = "Send SOP status update email")
    public ResponseEntity<String> sendSopStatusNotification(@RequestParam String email, @RequestParam String sopTitle, @RequestParam boolean approved) {
        CompletableFuture<Void> future = emailService.sendSopStatusNotification(email, sopTitle, approved);
        future.join(); // Wait for the email to be sent
        return ResponseEntity.ok("SOP status update email sent successfully");
    }
}
