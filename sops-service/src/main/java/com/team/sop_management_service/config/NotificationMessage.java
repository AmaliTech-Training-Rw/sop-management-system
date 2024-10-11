package com.team.sop_management_service.config;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    private String subject;
    private String recipient;
    private String message;
    private String type;


}
