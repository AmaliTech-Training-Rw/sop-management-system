package com.team.notifications_service.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.notifications_service.model.NotificationModel;
import com.team.notifications_service.repository.NotificationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @Autowired
    private NotificationEmail emailService;

    @Autowired
    SimpMessagingTemplate massagingTemplate;

    @Autowired
    private NotificationRepo notificationRepo;

    @KafkaListener(topics = "notification-events", groupId = "sop")
    public void consumeNotificationEvent(String eventMessage) {
        logger.info("Received notification {}", eventMessage);

        try {
            ObjectMapper mapper = new ObjectMapper();
            NotificationModel event = mapper.readValue(eventMessage, NotificationModel.class);

            notificationRepo.save(event);
            massagingTemplate.convertAndSend("/topic/notifications", event);

            if ("EMAIL".equalsIgnoreCase(event.getType())) {
                emailService.sendEmail(event.getRecipient(), event.getSubject(), event.getMessage());
            }


        } catch (Exception e) {
            logger.info("Error processing event: {}" , e.getMessage());
        }
    }
}

