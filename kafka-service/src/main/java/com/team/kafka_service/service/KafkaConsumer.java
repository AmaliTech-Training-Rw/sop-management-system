package com.team.kafka_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics = "sop-topic", groupId = "sop")
    public void consume(String message) {
        logger.info("Consumed msg: {}", message);
    }
}

