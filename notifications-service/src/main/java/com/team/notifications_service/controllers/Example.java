package com.team.notifications_service.controllers;

import com.team.notifications_service.kafkaServices.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class Example {
    KafkaProducer kafkaProducer;

    public Example(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }


    @GetMapping("/test/{id}")
    public String hello(@PathVariable int id) {
        kafkaProducer.sendMessage(
                "user-details-req",
                id
        );
        return "Check console";
    }
}
