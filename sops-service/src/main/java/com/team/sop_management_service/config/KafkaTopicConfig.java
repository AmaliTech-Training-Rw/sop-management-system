package com.team.sop_management_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic sopInitiationTopic() {
        return TopicBuilder.name("sop-initiation-topic").build();
    }
}
