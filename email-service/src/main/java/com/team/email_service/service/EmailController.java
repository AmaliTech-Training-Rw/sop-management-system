package com.team.email_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(EmailController.class);

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-verification")
    public String sendVerificationEmail(@RequestParam String to) {
        String token = emailService.generateToken();
        logger.info("Request to send verification email to: {}", to);
        emailService.sendVerificationEmail(to, token);
        logger.info("Verification email successfully sent to: {}", to);
        return "Verification email sent to " + to;
    }

    @PostMapping("/send-password-reset")
    public String sendPasswordResetEmail(@RequestParam String to) {
        String token = emailService.generateToken();
        logger.info("Request to send password reset email to: {}", to);
        emailService.sendPasswordResetEmail(to, token);
        logger.info("Password reset email successfully sent to: {}", to);
        return "Password reset email sent to " + to;
    }

    @PostMapping("/send-otp")
    public String sendOtpEmail(@RequestParam String to) {
        logger.info("Request to send OTP email to: {}", to);
        String otp = emailService.generateOtp();
        emailService.sendOtpEmail(to, otp);
        logger.info("OTP email successfully sent to: {}", to);
        return "OTP sent to " + to;
    }

    @PostMapping("/send-task-notification")
    public String sendTaskNotification(@RequestParam String to, @RequestParam String taskDescription) {
        logger.info("Request to send task notification email to: {}", to);
        emailService.sendTaskNotification(to, taskDescription);
        logger.info("Task notification email successfully sent to: {}", to);
        return "Task notification sent to " + to;
    }

    @PostMapping("/send-sop-return")
    public String sendSopReturnNotification(@RequestParam String to, @RequestParam String sopTitle) {
        logger.info("Request to send SOP return notification email to: {}", to);
        emailService.sendSopReturnNotification(to, sopTitle);
        logger.info("SOP return notification email successfully sent to: {}", to);
        return "SOP return notification sent to " + to;
    }

    @PostMapping("/send-sop-status")
    public String sendSopStatusNotification(@RequestParam String to, @RequestParam String sopTitle, @RequestParam boolean approved) {
        String status = approved ? "approved" : "rejected";
        logger.info("Request to send SOP status update ({}): {} to {}", status, sopTitle, to);
        emailService.sendSopStatusNotification(to, sopTitle, approved);
        logger.info("SOP status update email successfully sent to: {}", to);
        return "SOP status update sent to " + to;
    }

    @PostMapping("/send-new-sop")
    public String sendNewSopNotification(@RequestParam String to, @RequestParam String sopTitle) {
        logger.info("Request to send new SOP notification email to: {}", to);
        emailService.sendNewSopNotification(to, sopTitle);
        logger.info("New SOP notification email successfully sent to: {}", to);
        return "New SOP notification sent to " + to;
    }
}
