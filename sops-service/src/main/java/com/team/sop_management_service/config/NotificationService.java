//package com.team.sop_management_service.config;
//
//import com.team.sop_management_service.models.SOPInitiation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationService {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private static final String TOPIC = "notification-events";
//
//    @Autowired
//    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void notifyAuthor(SOPInitiation sop) {
//        String message = String.format("You have been assigned as the Author for SOP titled: %s", sop.getTitle());
//        String email = sop.getApprovalPipeline().getAuthor().getEmail();
//        sendNotification(email, message);
//    }
//
//    public void notifyReviewers(SOPInitiation sop) {
//        sop.getApprovalPipeline().getReviewers().forEach(reviewer -> {
//            String message = String.format("You have been assigned as a Reviewer for SOP titled: %s", sop.getTitle());
//            sendNotification(reviewer.getEmail(), message);
//        });
//    }
//
//    public void notifyApprover(SOPInitiation sop) {
//        String approverEmail = sop.getApprovalPipeline().getApprover().getEmail();
//        String message = String.format("You have been assigned as the Approver for SOP titled: %s", sop.getTitle());
//        sendNotification(approverEmail, message);
//    }
//
//    private void sendNotification(String email, String message) {
//        kafkaTemplate.send(TOPIC, email, message);
//    }
//}



package com.team.sop_management_service.config;

import com.team.sop_management_service.models.SOPInitiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "notification-events";

    @Autowired
    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void notifyAuthor(SOPInitiation sop) {
        String email = sop.getApprovalPipeline().getAuthor().getEmail();
        String sopTitle = sop.getTitle();
        String messageContent = String.format("You have been assigned as the Author for SOP titled: %s", sopTitle);
        String notificationType = "EMAIL";
        String subject = "SOP Author Assignment";


        NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
        sendNotification(email, message);
    }

    public void notifyReviewers(SOPInitiation sop) {
        sop.getApprovalPipeline().getReviewers().forEach(reviewer -> {
            String email = reviewer.getEmail();
            String sopTitle = sop.getTitle();
            String notificationType = "EMAIL";
            String messageContent = String.format("You have been assigned as the Reviewer for SOP titled: %s", sopTitle);
            String subject = "SOP Reviewer Assignment";

            NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
            sendNotification(email, message);
        });
    }

    public void notifyApprover(SOPInitiation sop) {
        String email = sop.getApprovalPipeline().getApprover().getEmail();
        String sopTitle = sop.getTitle();
        String messageContent = String.format("You have been assigned as the Approver for SOP titled: %s", sopTitle);
        String notificationType = "EMAIL";
        String subject = "SOP Approver Assignment";

        NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
        sendNotification(email, message);
    }

    private void sendNotification(String email, NotificationMessage message) {
        kafkaTemplate.send(TOPIC, email, message);
    }
}
