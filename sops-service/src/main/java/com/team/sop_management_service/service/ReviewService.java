//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.models.*;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.repository.UserRepository;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.config.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ReviewService {
//
//    private final SOPCreationRepository sopRepository;
//    private final SOPInitiationRepository sopInitiationRepository;
//    private final UserRepository userRepository;
//    private final NotificationService notificationService;
//    private final SOPCreationService sopCreationService;
//
//    @Transactional
//    public SOPCreation reviewSOP(String sopId, String reviewerId, boolean isConfirmed, String comment) {
//        // Fetch the SOPCreation and SOPInitiation
//        SOPCreation sop = sopRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
//        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
//                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));
//
//        // Verify that the reviewer is assigned to review this SOP
//        if (!sopInitiation.getApprovalPipeline().contains(reviewerId)) {
//            throw new RuntimeException("You are not assigned to review this SOP");
//        }
//
//        // Check if reviewer has already reviewed this SOP
//        boolean alreadyReviewed = sop.getReviews().stream()
//                .anyMatch(review -> review.getReviewerId().equals(reviewerId));
//
//        if (alreadyReviewed) {
//            throw new RuntimeException("You have already reviewed this SOP");
//        }
//
//        // Log the new review
//        Review review = new Review();
//        review.setReviewerId(reviewerId);
//        review.setConfirmed(isConfirmed);
//        review.setComment(comment);
//        review.setReviewedAt(LocalDateTime.now());
//
//        // Add the review to SOPCreation
//        sop.getReviews().add(review);
//        sop.setUpdatedAt(LocalDateTime.now());
//
//        // Handle notifications and status updates based on the review result
//        if (!isConfirmed) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.RETURNED);
//            notificationService.notifyAuthor(sop, "Your SOP has been returned with comments");
//        } else if (allReviewersConfirmed(sopInitiation, sop)) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.READY_FOR_APPROVAL);
//            notificationService.notifyApprover(sop, "An SOP is ready for your approval");
//        }
//
//        sopRepository.save(sop);
//        log.info("SOP reviewed: id={}, title={}, isConfirmed={}", sop.getId(), sop.getTitle(), isConfirmed);
//
//        return sop;
//    }
//
//    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
//        // Get the list of assigned reviewers
//        long assignedReviewersCount = sopInitiation.getAssignedReviewers().size();
//
//        // Get the number of confirmed reviews in SOPCreation
//        long confirmedReviewersCount = sop.getReviews().stream()
//                .filter(Review::isConfirmed)
//                .map(Review::getReviewerId)
//                .distinct()
//                .count();
//
//        // Check if all assigned reviewers have confirmed
//        return confirmedReviewersCount == assignedReviewersCount;
//    }
//}






//
//
//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.models.*;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.repository.UserRepository;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.config.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ReviewService {
//
//    private final SOPCreationRepository sopRepository;
//    private final SOPInitiationRepository sopInitiationRepository;
//    private final UserRepository userRepository;
//    private final NotificationService notificationService;
//    private final SOPCreationService sopCreationService;
//
//    @Transactional
//    public SOPCreation reviewSOP(String sopId, String reviewerId, boolean isConfirmed, String comment) {
//        // Fetch the SOPCreation and SOPInitiation
//        SOPCreation sop = sopRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
//        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
//                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));
//
//        // Verify that the reviewer is in the approval pipeline
//        if (!sopInitiation.getApprovalPipeline().getReviewers().contains(reviewerId)) {
//            throw new RuntimeException("You are not assigned to review this SOP");
//        }
//
//        // Check if reviewer has already reviewed this SOP
//        boolean alreadyReviewed = sop.getReviews().stream()
//                .anyMatch(review -> review.getReviewerId().equals(reviewerId));
//
//        if (alreadyReviewed) {
//            throw new RuntimeException("You have already reviewed this SOP");
//        }
//
//        // Log the new review
//        Review review = new Review();
//        review.setReviewerId(reviewerId);
//        review.setConfirmed(isConfirmed);
//        review.setComment(comment);
//        review.setReviewedAt(LocalDateTime.now());
//
//        // Add the review to SOPCreation
//        sop.getReviews().add(review);
//        sop.setUpdatedAt(LocalDateTime.now());
//
//        // Handle notifications and status updates based on the review result
//        if (!isConfirmed) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.RETURNED);
//            notificationService.notifyAuthor(sop, "Your SOP has been returned with comments");
//        } else if (allReviewersConfirmed(sopInitiation, sop)) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.READY_FOR_APPROVAL);
//            notificationService.notifyApprover(sop, "An SOP is ready for your approval");
//        }
//
//        sopRepository.save(sop);
//        log.info("SOP reviewed: id={}, title={}, isConfirmed={}", sop.getId(), sop.getTitle(), isConfirmed);
//
//        return sop;
//    }
//
//    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
//        // Get the list of users in the approval pipeline
//        long pipelineUsersCount = sopInitiation.getApprovalPipeline().size();
//
//        // Get the number of confirmed reviews in SOPCreation
//        long confirmedReviewersCount = sop.getReviews().stream()
//                .filter(Review::isConfirmed)
//                .map(Review::getReviewerId)
//                .distinct()
//                .count();
//
//        // Check if all users in the approval pipeline have confirmed
//        return confirmedReviewersCount == pipelineUsersCount;
//    }
//}















package com.team.sop_management_service.service;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.models.*;
import com.team.sop_management_service.repository.ReveiwerReppository;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import com.team.sop_management_service.repository.UserRepository;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.config.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final SOPCreationRepository sopRepository;
    private final SOPInitiationRepository sopInitiationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SOPCreationService sopCreationService;
    private final ReveiwerReppository reveiwerReppository;

    @Transactional
    public SOPCreation reviewSOP(String sopId, String reviewerId, boolean isConfirmed, String comment) {
        // Fetch the SOPCreation and SOPInitiation
        SOPCreation sop = sopRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));

        // Fetch the reviewer User object
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        // Verify that the reviewer is in the approval pipeline
        if (!sopInitiation.getApprovalPipeline().getReviewers().contains(reviewerId)) {
            throw new RuntimeException("You are not assigned to review this SOP");
        }

        // Check if the reviewer has already reviewed this SOP
        boolean alreadyReviewed = sop.getReviews().stream()
                .anyMatch(review -> review.getReviewerId().getId().equals(reviewerId));

        if (alreadyReviewed) {
            throw new RuntimeException("You have already reviewed this SOP");
        }

        // Log the new review
        Review review = new Review();
        review.setReviewerId(reviewer);  // Setting the User object, not just reviewerId
        review.setConfirmed(isConfirmed);
        review.setComment(comment);
        review.setReviewedAt(LocalDateTime.now());

        // Add the review to SOPCreation
        sop.getReviews().add(review);
        sop.setUpdatedAt(LocalDateTime.now());

        // Handle notifications and status updates based on the review result
        if (!isConfirmed) {
            sopCreationService.updateSOPStatus(sopId, SOPStatus.REJECTED);
            //notificationService.notifyAuthor(sop, "Your SOP has been returned with comments");
        } else if (allReviewersConfirmed(sopInitiation, sop)) {
            sopCreationService.updateSOPStatus(sopId, SOPStatus.REVIEW);       // notificationService.notifyApprover(sop, "An SOP is ready for your approval");
        }

        sopRepository.save(sop);
        reveiwerReppository.save(review);
        log.info("SOP reviewed: id={}, title={}, isConfirmed={}", sop.getId(), sop.getTitle(), isConfirmed);

        return sop;
    }

    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
        // Get the list of reviewers in the approval pipeline
        long pipelineUsersCount = sopInitiation.getApprovalPipeline().getReviewers().size();

        // Get the number of confirmed reviews in SOPCreation
        long confirmedReviewersCount = sop.getReviews().stream()
                .filter(Review::isConfirmed)
                .map(review -> review.getReviewerId().getId())  // Getting the reviewer ID from the User object
                .distinct()
                .count();

        // Check if all reviewers in the approval pipeline have confirmed
        return confirmedReviewersCount == pipelineUsersCount;
    }
}
