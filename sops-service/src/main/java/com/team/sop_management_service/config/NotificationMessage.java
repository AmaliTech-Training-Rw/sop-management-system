package com.team.sop_management_service.config;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    private String subject;
    private String email;
    private String sopTitle;
    private String messageContent;
    private String notificationType;


}
