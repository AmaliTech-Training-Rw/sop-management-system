package com.team.authentication_service.kafkaServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.authentication_service.dtos.UserDto;
import com.team.authentication_service.services.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper;
    private UserService userService;
    private KafkaProducer kafkaProducer;

    public KafkaConsumer(ObjectMapper objectMapper, UserService userService, KafkaProducer kafkaProducer) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "user-details-req", errorHandler = "customErrorHandler", groupId = "auth-service")
    public void consumeUserDetailsConsumer(ConsumerRecord<String, String> record) {
        try {
//          Sending the user details via another producer
            UserDto userDto = userService.getUserById(Integer.parseInt(record.value()));
            kafkaProducer.sendMessage("user-details-res", userDto);
        } catch (Exception e) {
            logger.error("Error processing email request", e);
        }
    }


}
