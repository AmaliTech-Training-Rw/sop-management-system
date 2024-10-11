package com.team.sop_management_service.service;

import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.Review;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {
    private final SOPCreationRepository sopRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SOPCreationService sopCreationService;

    @Transactional
    public SOPCreation approveOrRejectSOP(String sopId, String approverId, boolean isApproved, String comment) {
        SOPCreation sop = sopRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (!approver.equals(sop.getSopInitiation().getApprovalPipeline().getApprover())) {
            throw new RuntimeException("You are not assigned to approve this SOP");
        }

        sop.setApproved(isApproved);
        SOPStatus newStatus = isApproved ? SOPStatus.APPROVED : SOPStatus.REJECTED;
        sopCreationService.updateSOPStatus(sopId, newStatus);

        Review approvalReview = new Review();
        approvalReview.setReviewerId(approver);
        approvalReview.setConfirmed(isApproved);

        sop.getReviews().add(approver);

        sopRepository.save(sop);
        log.info("SOP {}: id={}, title={}", isApproved ? "approved" : "rejected", sop.getId(), sop.getTitle());

       // notificationService.notifyAllParticipants(sop, "SOP has been " + (isApproved ? "approved" : "rejected"));
        return sop;
    }
}