//package com.team.sop_management_service.service;
//
////import com.team.sop_management_service.config.NotificationService;
//import com.team.sop_management_service.enums.SOPStatus;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.Review;
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.models.User;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import com.team.sop_management_service.repository.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ApprovalService {
//
//    // Repositories to access SOP and User data
//    private final SOPCreationRepository sopRepository;
//    private final UserRepository userRepository;
//
//    // Service for sending notifications
//  //  private final NotificationService notificationService;
//
//    // Service for updating SOP status
//    private final SOPCreationService sopCreationService;
//
//    //Approves or rejects an SOP based on the provided parameters.
//
//    @Transactional
//    public SOPCreation approveOrRejectSOP(String sopId, String approverId, boolean isApproved) {
//
//        // Find the SOP by its ID or throw an exception if not found
//        SOPCreation sop = sopRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
//
//        // Find the approver (user) by their ID or throw an exception if not found
//        User approver = userRepository.findById(approverId)
//                .orElseThrow(() -> new RuntimeException("Approver not found"));
//
//        // Check if the approver is the correct one from the approval pipeline
//        if (!approver.equals(sop.getSopInitiation().getApprovalPipeline().getApprover())) {
//            throw new RuntimeException("You are not assigned to approve this SOP");
//        }
//
//        // Check if the SOP has been reviewed before it can be approved or rejected
//        if (sop.getReviews().isEmpty()) {
//            throw new RuntimeException("SOP cannot be approved/rejected without being reviewed");
//        }
//
//        // Set approval status (true or false) for the SOP
//        sop.setApproved(isApproved);
//
//        // Determine the new status based on approval/rejection
//        SOPStatus newStatus = isApproved ? SOPStatus.APPROVED : SOPStatus.REJECTED;
//
//        // Update the SOP status through the service layer
//        sopCreationService.updateSOPStatus(sopId, newStatus);
//
//        // Create a new review object to record the approver's decision
//        Review approvalReview = new Review();
//        approvalReview.setReviewerId(approver);  // Set approver's ID
//        approvalReview.setConfirmed(isApproved); // Set review confirmation status
//
//        // Add the approval review to the SOP's list of reviews
//        sop.getReviews().add(approver);
//
//        // Save the updated SOP back to the repository
//        sopRepository.save(sop);
//
//        // Log the result of the approval/rejection for auditing
//        log.info("SOP {}: id={}, title={}", isApproved ? "approved" : "rejected", sop.getId(), sop.getTitle());
//
//        // Optionally notify all participants of the SOP about the decision
//        // notificationService.notifyAllParticipants(sop, "SOP has been " + (isApproved ? "approved" : "rejected"));
//
//        return sop;  // Return the updated SOP object
//    }
//}


package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {

    private final SOPCreationRepository sopRepository;
    private final SOPCreationService sopCreationService;
    private final AuthenticationServiceClient authService;

    @Transactional
    public SOPCreation approveOrRejectSOP(String sopId, int approverId, boolean isApproved) {
        SOPCreation sop = sopRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));

        if (sop.getStatus() != SOPStatus.REVIEW) {
            throw new IllegalStateException("Cannot approve/reject an SOP that has not been reviewed");
        }

        UserDto approver = authService.getUserById(approverId).getBody();
        if (approver == null) {
            throw new RuntimeException("Approver not found");
        }

        if (sop.getSopInitiation().getApprovalPipeline().getApprover() != approverId) {
            throw new RuntimeException("You are not assigned to approve this SOP");
        }

        SOPStatus newStatus = isApproved ? SOPStatus.APPROVED : SOPStatus.REJECTED;
        sopCreationService.updateSOPStatus(sopId, newStatus);

        sop.getReviews().add(approverId);

        log.info("SOP {}: id={}, title={}", isApproved ? "approved" : "rejected", sop.getId(), sop.getTitle());

        return sopRepository.save(sop);
    }
}