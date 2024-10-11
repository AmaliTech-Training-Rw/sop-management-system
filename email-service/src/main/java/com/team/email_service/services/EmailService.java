package com.team.email_service.services;

import com.team.email_service.dtos.SendEmailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final MailSenderAutoConfiguration mailSenderAutoConfiguration;

    public EmailService(@Value("${spring.mail.host}") String host,
                        @Value("${spring.mail.port}") int port,
                        @Value("${spring.mail.username}") String username,
                        @Value("${spring.mail.password}") String password, MailSenderAutoConfiguration mailSenderAutoConfiguration) {
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
        this.mailSenderAutoConfiguration = mailSenderAutoConfiguration;
    }

    public void sendEmail(SendEmailRequestDto emailRequestDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRequestDto.getRecipient());
            message.setSubject(emailRequestDto.getSubject());
            message.setText(emailRequestDto.getBody());
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlEmail(SendEmailRequestDto emailRequestDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailRequestDto.getRecipient());
        helper.setSubject(emailRequestDto.getSubject());
        helper.setText(emailRequestDto.getBody(), true);

        mailSender.send(message);
    }

}
