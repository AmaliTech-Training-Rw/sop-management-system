//package com.team.email_service.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.stereotype.Service;
//
//import java.util.Properties;
//import java.util.Random;
//import java.util.UUID;
//
//@Service
//public class EmailService {
//    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
//    private final JavaMailSender mailSender;
//    private final String baseUrl;
//
//    public EmailService(@Value("${spring.mail.host}") String host,
//                        @Value("${spring.mail.port}") int port,
//                        @Value("${spring.mail.username}") String username,
//                        @Value("${spring.mail.password}") String password,
//                        @Value("${app.base-url}") String baseUrl) {
//        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
//        mailSenderImpl.setHost(host);
//        mailSenderImpl.setPort(port);
//        mailSenderImpl.setUsername(username);
//        mailSenderImpl.setPassword(password);
//        Properties props = mailSenderImpl.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true");
//        props.put("mail.smtp.ssl.trust", host);
//        this.mailSender = mailSenderImpl;
//        this.baseUrl = baseUrl;
//
//        logger.info("EmailService initialized with base URL: {}", baseUrl);
//    }
//
//    public void sendVerificationEmail(String to, String token) {
//        logger.info("Sending verification email to: {}", to);
//        String verificationUrl = baseUrl + "/auth/verify?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Email Verification");
//        message.setText("Please verify your email by clicking the following link: " + verificationUrl);
//        mailSender.send(message);
//        logger.info("Verification email sent to {}", to);
//    }
//
//    public void sendPasswordResetEmail(String to, String token) {
//        logger.info("Sending password reset email to: {}", to);
//        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Password Reset");
//        message.setText("Click the following link to reset your password: " + resetUrl);
//        mailSender.send(message);
//        logger.info("Password reset email sent to {}", to);
//    }
//
//    public void sendOtpEmail(String to, String otp) {
//        logger.info("Sending OTP email to: {}", to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("One-Time Password for Password Reset");
//        message.setText("Your OTP for password reset is: " + otp);
//        mailSender.send(message);
//        logger.info("OTP email sent to {}", to);
//    }
//
//    public void sendTaskNotification(String to, String taskDescription) {
//        logger.info("Sending task notification email to: {}", to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("New Task Assignment");
//        message.setText("You have been assigned a new task: " + taskDescription);
//        mailSender.send(message);
//        logger.info("Task notification email sent to {}", to);
//    }
//
//    public void sendSopReturnNotification(String to, String sopTitle) {
//        logger.info("Sending SOP return notification email to: {}", to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("SOP Returned");
//        message.setText("The SOP '" + sopTitle + "' has been returned for revision.");
//        mailSender.send(message);
//        logger.info("SOP return notification email sent to {}", to);
//    }
//
//    public void sendSopStatusNotification(String to, String sopTitle, boolean approved) {
//        String status = approved ? "approved" : "rejected";
//        logger.info("Sending SOP status update ({}): {} to {}", status, sopTitle, to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("SOP Status Update");
//        message.setText("The SOP '" + sopTitle + "' has been " + status + ".");
//        mailSender.send(message);
//        logger.info("SOP status notification email sent to {}", to);
//    }
//
//    public void sendNewSopNotification(String to, String sopTitle) {
//        logger.info("Sending new SOP notification email to: {}", to);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("New SOP Published");
//        message.setText("A new SOP titled '" + sopTitle + "' has been published.");
//        mailSender.send(message);
//        logger.info("New SOP notification email sent to {}", to);
//    }
//
//    public String generateOtp() {
//        logger.debug("Generating OTP");
//        Random random = new Random();
//        String otp = String.format("%06d", random.nextInt(1000000));
//        logger.info("Generated OTP: {}", otp);
//        return otp;
//    }
//
//    public String generateToken() {
//        String token = UUID.randomUUID().toString();
//        logger.info("Generated token: {}", token);
//        return token;
//    }
//}


package com.team.email_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
//@Api(tags = "Email Service", description = "Handles all email-related operations")
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final String baseUrl;

    public EmailService(@Value("${spring.mail.host}") String host,
                        @Value("${spring.mail.port}") int port,
                        @Value("${spring.mail.username}") String username,
                        @Value("${spring.mail.password}") String password,
                        @Value("${app.base-url}") String baseUrl) {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(host);
        mailSenderImpl.setPort(port);
        mailSenderImpl.setUsername(username);
        mailSenderImpl.setPassword(password);
        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", host);
        this.mailSender = mailSenderImpl;
        this.baseUrl = baseUrl;

        logger.info("EmailService initialized with base URL: {}", baseUrl);
    }

    @Async
    @Retryable(value = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    //@ApiOperation("Send account creation email with password setup link")
    public CompletableFuture<Void> sendAccountCreationEmail(String to, String token) {
        logger.info("Sending account creation email to: {}", to);
        String setupUrl = baseUrl + "/auth/setup-password?token=" + token;
        String subject = "Account Creation - Set Up Your Password";
        String content = "Welcome! Click the following link to set up your password: " + setupUrl;
        return sendEmail(to, subject, content);
    }

    @Async
    @Retryable(value = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    //@ApiOperation("Send password reset email with OTP")
    public CompletableFuture<Void> sendPasswordResetEmail(String to, String otp) {
        logger.info("Sending password reset email to: {}", to);
        String subject = "Password Reset - One-Time Password";
        String content = "Your OTP for password reset is: " + otp + ". Use this to reset your password.";
        return sendEmail(to, subject, content);
    }

    @Async
    @Retryable(value = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
   // @ApiOperation("Send task notification to author")
    public CompletableFuture<Void> sendTaskNotification(String to, String taskDescription) {
        logger.info("Sending task notification email to: {}", to);
        String subject = "New Task Assignment";
        String content = "You have been assigned a new task: " + taskDescription;
        return sendEmail(to, subject, content);
    }

    @Async
    @Retryable(value = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
   // @ApiOperation("Send SOP status update notification")
    public CompletableFuture<Void> sendSopStatusNotification(String to, String sopTitle, boolean approved) {
        String status = approved ? "approved" : "rejected";
        logger.info("Sending SOP status update ({}): {} to {}", status, sopTitle, to);
        String subject = "SOP Status Update";
        String content = "The SOP '" + sopTitle + "' has been " + status + ".";
        return sendEmail(to, subject, content);
    }

    private CompletableFuture<Void> sendEmail(String to, String subject, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(content);
                mailSender.send(message);
                logger.info("Email sent successfully to {}", to);
            } catch (MailException e) {
                logger.error("Failed to send email to {}", to, e);
                throw e;
            }
        });
    }

   // @ApiOperation("Generate OTP for password reset")
    public String generateOtp() {
        logger.debug("Generating OTP");
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        logger.info("Generated OTP: {}", otp);
        return otp;
    }

   // @ApiOperation("Generate token for email verification")
    public String generateToken() {
        String token = UUID.randomUUID().toString();
        logger.info("Generated token: {}", token);
        return token;
    }
}