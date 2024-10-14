package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.Review;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    private final SOPCreationRepository sopRepository;
    private final SOPInitiationRepository sopInitiationRepository;
    private final SOPCreationService sopCreationService;
    private final AuthenticationServiceClient authService;
    private final NotificationService notificationService;

    @Autowired
    public ReviewService(SOPCreationRepository sopRepository,
                         SOPInitiationRepository sopInitiationRepository,
                         SOPCreationService sopCreationService,
                         NotificationService notificationService,
                         AuthenticationServiceClient authService) {
        this.sopRepository = sopRepository;
        this.sopInitiationRepository = sopInitiationRepository;
        this.sopCreationService = sopCreationService;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    @Transactional
    public SOPCreation reviewSOP(String sopId, int reviewerId, boolean isConfirmed, String comment) {
        // Fetch the SOP and SOPInitiation
        SOPCreation sop = sopRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
        SOPInitiation sopInitiation = sopInitiationRepository.findById(sop.getSopInitiation().getSopId())
                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));

        // Ensure SOP has been submitted
        if (sop.getStatus() != SOPStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot review an SOP that has not been submitted");
        }

        // Fetch the reviewer User object using Feign client
        UserDto reviewerUser = authService.getUserById(reviewerId).getBody();
        if (reviewerUser == null) {
            throw new RuntimeException("Reviewer not found");
        }

        // Check if the reviewer is in the approval pipeline
        if (sopInitiation.getApprovalPipeline().getReviewers().stream()
                .noneMatch(id -> id.equals(reviewerId))) {
            throw new RuntimeException("You are not assigned to review this SOP");
        }

        // Ensure the reviewer hasn't reviewed it already
        if (sop.getReviews().contains(reviewerId)) {
            throw new RuntimeException("You have already reviewed this SOP");
        }

        // Create the new review using Builder Pattern
        Review newReview = Review.builder()
                .reviewerId(reviewerId)
                .isConfirmed(isConfirmed)
                // .comment(comment) // Add the comment if needed
                .build();

        // Initialize reviews list if it's null
        if (sop.getReviews() == null) {
            sop.setReviews(new ArrayList<>());
        }
        // Add the review object, not just the reviewerId
        sop.getReviews().add(newReview.getReviewerId());

        // Set the updated timestamp
        sop.setUpdatedAt(LocalDateTime.now());

        // Handle status based on the review result
        if (!isConfirmed) {
            sopCreationService.updateSOPStatus(sopId, SOPStatus.REJECTED);
            // notificationService.sendNotificationToAuthor(sopId, "SOP Rejected", comment);
        } else if (allReviewersConfirmed(sopInitiation, sop)) {
            sopCreationService.updateSOPStatus(sopId, SOPStatus.REVIEWED);
           //  notificationService.notifyApprover();
        }

        // Save the updated SOPCreation
        sopRepository.save(sop);
      //  notificationService.notifyAuthor(sop);

        return sop;
    }


    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
        // Get the list of reviewer IDs from the approval pipeline (already a list of integers)
        List<Integer> pipelineReviewerIds = sopInitiation.getApprovalPipeline().getReviewers();

        // Get the list of confirmed reviewer IDs from the reviews
        List<Integer> confirmedReviewerIds = sop.getReviews(); // This is already a List<Integer>

        // Check if all pipeline reviewers have confirmed
        return confirmedReviewerIds.containsAll(pipelineReviewerIds)
                && pipelineReviewerIds.size() == confirmedReviewerIds.size();
    }

}
