package com.team.sop_management_service.config;

import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuthenticationServiceClient authenticationServiceClient;
    private static final String TOPIC = "notification-events";

    @Autowired
    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate, AuthenticationServiceClient authenticationServiceClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.authenticationServiceClient = authenticationServiceClient;
    }

    public void notifyAuthor(SOPInitiation sop) {
        UserDto author = getUserById(sop.getApprovalPipeline().getAuthor());
        String recipient = author.getEmail();
        String message = String.format("You have been assigned as the Author for SOP titled: %s", sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Author Assignment";

        NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyReviewers(SOPInitiation sop) {
        sop.getApprovalPipeline().getReviewers().forEach(reviewer -> {
            UserDto reviewerDto = getUserById(reviewer);
            String recipient = reviewerDto.getEmail();
            String type = "EMAIL";
            String message = String.format("You have been assigned as the Reviewer for SOP titled: %s", sop.getTitle());
            String subject = "SOP Reviewer Assignment";

            NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
            sendNotification(recipient, msg);
        });
    }

    public void notifyApprover(SOPInitiation sop) {
        UserDto approver = getUserById(sop.getApprovalPipeline().getApprover());
        String recipient = approver.getEmail();
        String message = String.format("You have been assigned as the Approver for SOP titled: %s", sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Approver Assignment";

        NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyAuthorOfReviewerComment(SOPCreation sop) {
        UserDto author = getUserById(sop.getSopInitiation().getApprovalPipeline().getAuthor());
        String recipient = author.getEmail();
        String message = String.format("Your SOP titled '%s' has received a comment from a reviewer. Please review and make necessary changes.", sop.getTitle());
        String type = "EMAIL";
        String subject = "SOP Review Comment Received";

        NotificationMessage msg = new NotificationMessage(subject, recipient, message, type);
        sendNotification(recipient, msg);
    }

    public void notifyAllOfApproval(SOPCreation sop) {
        List<UserDto> allInvolved = getAllInvolvedUsers(sop);
        String sopTitle = sop.getTitle();
        String message = String.format("SOP titled '%s' has been approved.", sopTitle);
        String type = "EMAIL";
        String subject = "SOP Approved";

        allInvolved.forEach(user -> {
            NotificationMessage msg = new NotificationMessage(subject, user.getEmail(), message, type);
            sendNotification(user.getEmail(), msg);
        });
    }

    public void notifyAllOfRejection(SOPCreation sop, String rejectionReason) {
        List<UserDto> allInvolved = getAllInvolvedUsers(sop);
        String sopTitle = sop.getTitle();
        String messageContent = String.format("SOP titled '%s' has been rejected. Reason: %s", sopTitle, rejectionReason);
        String notificationType = "EMAIL";
        String subject = "SOP Rejected";

        allInvolved.forEach(user -> {
            NotificationMessage message = new NotificationMessage(subject, user.getEmail(), messageContent, notificationType);
            sendNotification(user.getEmail(), message);
        });
    }

    private List<Integer> getAllInvolvedUserIds(SOPCreation sop) {
        List<Integer> userIds = new ArrayList<>();

        // Add author ID
        userIds.add(sop.getSopInitiation().getApprovalPipeline().getAuthor());

//        // Add reviewer IDs
//        userIds.addAll(sop.getSopInitiation().getApprovalPipeline().getReviewers().stream()
//                .map(UserDto::getId)
//                .collect(Collectors.toList()));

        // Add approver ID
        userIds.add(sop.getSopInitiation().getApprovalPipeline().getApprover());

        return userIds;
    }

    private List<UserDto> getAllInvolvedUsers(SOPCreation sop) {
        return getAllInvolvedUserIds(sop).stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private UserDto getUserById(int userId) {
        ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch user with ID: " + userId);
        }
    }

    private void sendNotification(String email, NotificationMessage message) {
        kafkaTemplate.send(TOPIC, email, message);
    }
}