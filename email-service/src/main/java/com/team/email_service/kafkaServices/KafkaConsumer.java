package com.team.email_service.kafkaServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.email_service.dtos.SendEmailRequestDto;
import com.team.email_service.services.EmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper;
    EmailService emailService;

    public KafkaConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-service-send", errorHandler = "customErrorHandler", groupId = "email-service")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String jsonPayload = record.value();
            SendEmailRequestDto emailRequest = objectMapper.readValue(jsonPayload, SendEmailRequestDto.class);
            emailService.sendHtmlEmail(
                    SendEmailRequestDto.builder()
                            .recipient(emailRequest.getRecipient())
                            .subject(emailRequest.getSubject())
                            .body(emailRequest.getBody())
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error processing email request", e);
        }
    }
}