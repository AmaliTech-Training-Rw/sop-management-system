package com.team.authentication_service.kafkaServices;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        System.out.println("#######################");
        System.out.println(message);
        System.out.println("#######################");

        kafkaTemplate.send(topic, message);
    }
}
