package com.team.notifications_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "NotificationModel")
public class NotificationModel {
    private String id;
    private String type;
    private String recipient;
    private String subject;
    private String message;
    private boolean read = false;
}

