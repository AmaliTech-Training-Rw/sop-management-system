package com.team.notifications_service.kafkaServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.notifications_service.dtos.DepartmentDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private ObjectMapper mapper;

    public KafkaConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(topics = "user-details-res", errorHandler = "customErrorHandler", groupId = "notifications-service")
    public void listen(ConsumerRecord<String, DepartmentDto> record) {
        System.out.println("#######################");
        System.out.println("Notification Listener for the user details");
        System.out.println(record.value());
        System.out.println("#######################");
        System.out.println(record.value());
    }
}
