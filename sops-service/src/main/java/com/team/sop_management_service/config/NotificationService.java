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

//
//package com.team.sop_management_service.config;
//
//import com.team.sop_management_service.models.SOPInitiation;
//import com.team.sop_management_service.models.SOP;
//import com.team.sop_management_service.models.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
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
//        String email = sop.getApprovalPipeline().getAuthor().getEmail();
//        String sopTitle = sop.getTitle();
//        String messageContent = String.format("You have been assigned as the Author for SOP titled: %s. Please add description, category, content, and files.", sopTitle);
//        String notificationType = "EMAIL";
//        String subject = "New SOP Assignment";
//
//        NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
//        sendNotification(email, message);
//    }
//
//    public void notifyReviewers(SOP sop) {
//        List<User> reviewers = sop.getApprovalPipeline().getReviewers();
//        reviewers.forEach(reviewer -> {
//            String email = reviewer.getEmail();
//            String sopTitle = sop.getTitle();
//            String notificationType = "EMAIL";
//            String messageContent = String.format("A new SOP titled '%s' has been submitted for your review. Please view, comment, and confirm or request changes.", sopTitle);
//            String subject = "New SOP Ready for Review";
//
//            NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
//            sendNotification(email, message);
//        });
//    }
//
//    public void notifyApprover(SOP sop) {
//        String email = sop.getApprovalPipeline().getApprover().getEmail();
//        String sopTitle = sop.getTitle();
//        String messageContent = String.format("SOP titled '%s' is ready for your final approval. Please review and approve or reject.", sopTitle);
//        String notificationType = "EMAIL";
//        String subject = "SOP Ready for Approval";
//
//        NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
//        sendNotification(email, message);
//    }
//
//    public void notifyAuthorOfReviewerComment(SOP sop, String reviewerComment) {
//        String email = sop.getApprovalPipeline().getAuthor().getEmail();
//        String sopTitle = sop.getTitle();
//        String messageContent = String.format("Your SOP titled '%s' has received a comment from a reviewer. Please review and make necessary changes.", sopTitle);
//        String notificationType = "EMAIL";
//        String subject = "SOP Review Comment Received";
//
//        NotificationMessage message = new NotificationMessage(subject, email, sopTitle, messageContent, notificationType);
//        sendNotification(email, message);
//    }
//
//    public void notifyAllOfApproval(SOP sop) {
//        List<User> allInvolved = getAllInvolvedUsers(sop);
//        String sopTitle = sop.getTitle();
//        String messageContent = String.format("SOP titled '%s' has been approved.", sopTitle);
//        String notificationType = "EMAIL";
//        String subject = "SOP Approved";
//
//        allInvolved.forEach(user -> {
//            NotificationMessage message = new NotificationMessage(subject, user.getEmail(), sopTitle, messageContent, notificationType);
//            sendNotification(user.getEmail(), message);
//        });
//    }
//
//    public void notifyAllOfRejection(SOP sop, String rejectionReason) {
//        List<User> allInvolved = getAllInvolvedUsers(sop);
//        String sopTitle = sop.getTitle();
//        String messageContent = String.format("SOP titled '%s' has been rejected. Reason: %s", sopTitle, rejectionReason);
//        String notificationType = "EMAIL";
//        String subject = "SOP Rejected";
//
//        allInvolved.forEach(user -> {
//            NotificationMessage message = new NotificationMessage(subject, user.getEmail(), sopTitle, messageContent, notificationType);
//            sendNotification(user.getEmail(), message);
//        });
//    }
//
//    private List<User> getAllInvolvedUsers(SOP sop) {
//        List<User> involved = new ArrayList<>();
//        involved.add(sop.getApprovalPipeline().getAuthor());
//        involved.addAll(sop.getApprovalPipeline().getReviewers());
//        involved.add(sop.getApprovalPipeline().getApprover());
//        return involved;
//    }
//
//    private void sendNotification(String email, NotificationMessage message) {
//        kafkaTemplate.send(TOPIC, email, message);
//    }
//}