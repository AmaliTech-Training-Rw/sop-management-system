package com.team.notifications_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationModel {
    private String type;
    private String recipient;
    private String subject;
    private String message;
}

