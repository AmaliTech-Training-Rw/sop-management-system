package com.team.messaging_service.config;

import com.team.messaging_service.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentProducer {

    private final KafkaTemplate<String, Comment> kafkaTemplate;
    private static final String TOPIC = "comments_topic";

    @Autowired
    public CommentProducer(KafkaTemplate<String, Comment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendComment(Comment comment) {
        kafkaTemplate.send(TOPIC, comment);
    }
}