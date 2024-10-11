////package com.team.sop_management_service.config;
////
////import com.team.sop_management_service.models.SOPInitiation;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.kafka.core.KafkaTemplate;
////import org.springframework.stereotype.Service;
////
////@Service
////public class NotificationService {
////
////    private final KafkaTemplate<String, Object> kafkaTemplate;
////    private static final String TOPIC = "notification-events";
////
////    @Autowired
////    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
////        this.kafkaTemplate = kafkaTemplate;
////    }
////
////    public void notifyAuthor(SOPInitiation sop) {
////        String message = String.format("You have been assigned as the Author for SOP titled: %s", sop.getTitle());
////        String email = sop.getApprovalPipeline().getAuthor().getEmail();
////        sendNotification(email, message);
////    }
////
////    public void notifyReviewers(SOPInitiation sop) {
////        sop.getApprovalPipeline().getReviewers().forEach(reviewer -> {
////            String message = String.format("You have been assigned as a Reviewer for SOP titled: %s", sop.getTitle());
////            sendNotification(reviewer.getEmail(), message);
////        });
////    }
////
////    public void notifyApprover(SOPInitiation sop) {
////        String approverEmail = sop.getApprovalPipeline().getApprover().getEmail();
////        String message = String.format("You have been assigned as the Approver for SOP titled: %s", sop.getTitle());
////        sendNotification(approverEmail, message);
////    }
////
////    private void sendNotification(String email, String message) {
////        kafkaTemplate.send(TOPIC, email, message);
////    }
////}
//
//

package com.team.sop_management_service.config;

import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "notification-events";

    @Autowired
    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void notifyAuthor(SOPInitiation sop) {
        String recipient = sop.getApprovalPipeline().getAuthor().getEmail();
        String message = String.format("You have been assigned as the Author for SOP titled: %s", sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Author Assignment";


        NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyReviewers(SOPInitiation sop) {
        sop.getApprovalPipeline().getReviewers().forEach(reviewer -> {
            String recipient = reviewer.getEmail();
            String type = "EMAIL";
            String message = String.format("You have been assigned as the Reviewer for SOP titled: %s",sop.getTitle());
            String subject = "SOP Reviewer Assignment";

            NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
            sendNotification(recipient, msg);
        });
    }

    public void notifyApprover(SOPInitiation sop) {
        String recipient = sop.getApprovalPipeline().getApprover().getEmail();
        String message = String.format("You have been assigned as the Approver for SOP titled: %s",sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Approver Assignment";

        NotificationMessage msg = new NotificationMessage(subject,recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyAuthorOfReviewerComment(SOPCreation sop) {
        String recipient = sop.getSopInitiation().getApprovalPipeline().getAuthor().getEmail();
        String message = String.format("Your SOP titled '%s' has received a comment from a reviewer. Please review and make necessary changes.", sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Review Comment Received";

        NotificationMessage msg = new NotificationMessage(subject,recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyAllOfApproval(SOPCreation sop) {
        List<User> allInvolved = getAllInvolvedUsers(sop);
        String sopTitle = sop.getTitle();
        String message = String.format("SOP titled '%s' has been approved.", sopTitle);
        String type = "EMAIL";
        String subject = "SOP Approved";

//        allInvolved.forEach(user -> {
//            NotificationMessage msg = new NotificationMessage(subject,, message, type);
//            sendNotification(allInvolved, msg);
//        });
    }

    public void notifyAllOfRejection(SOPCreation sop, String rejectionReason) {
        List<User> allInvolved = getAllInvolvedUsers(sop);
        String sopTitle = sop.getTitle();
        String messageContent = String.format("SOP titled '%s' has been rejected. Reason: %s", sopTitle, rejectionReason);
        String notificationType = "EMAIL";
        String subject = "SOP Rejected";

        allInvolved.forEach(user -> {
//            NotificationMessage message = new NotificationMessage(subject, user.getEmail(), sopTitle, messageContent, notificationType);
//            sendNotification(user.getEmail(), message);
        });
    }

    private List<User> getAllInvolvedUsers(SOPCreation sop) {
        List<User> involved = new ArrayList<>();
        involved.add(sop.getSopInitiation(). getApprovalPipeline().getAuthor());
        involved.addAll(sop.getSopInitiation(). getApprovalPipeline().getReviewers());
        involved.add(sop.getSopInitiation(). getApprovalPipeline().getApprover());
        return involved;
    }

    private void sendNotification(String email, NotificationMessage message) {
        kafkaTemplate.send(TOPIC, email, message);
    }
}
