package com.team.authentication_service.utils.errorHandlers;

import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorHandler implements KafkaListenerErrorHandler {
    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException e) {
        // Log the error and handle it as needed
        System.err.println("Error processing message: " + message);
        System.err.println("Error: " + e.getMessage());
        return null; // or handle accordingly
    }
}
