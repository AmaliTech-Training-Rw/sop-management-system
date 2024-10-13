//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.enums.SOPStatus;
//import com.team.sop_management_service.models.*;
//import com.team.sop_management_service.repository.ReveiwerReppository;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.repository.UserRepository;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.config.NotificationService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class ReviewService {
//
//    private final SOPCreationRepository sopRepository;
//    private final SOPInitiationRepository sopInitiationRepository;
//    private final UserRepository userRepository;
//    private final NotificationService notificationService;
//    private final SOPCreationService sopCreationService;
//    private final ReveiwerReppository reveiwerReppository;
//    @Autowired
//   public ReviewService(SOPCreationRepository sopRepository, SOPInitiationRepository sopInitiationRepository, UserRepository userRepository, NotificationService notificationService, SOPCreationService sopCreationService, ReveiwerReppository reveiwerReppository) {
//        this.sopRepository = sopRepository;
//        this.sopInitiationRepository = sopInitiationRepository;
//        this.userRepository = userRepository;
//        this.notificationService = notificationService;
//        this.sopCreationService = sopCreationService;
//        this.reveiwerReppository = reveiwerReppository;
//    }
//
//    @Transactional
//    public SOPCreation reviewSOP(String sopId, String reviewerId, boolean isConfirmed) {
//        // Fetch the SOPCreation and SOPInitiation
//        SOPCreation sop = sopRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
//        SOPInitiation sopInitiation = sopInitiationRepository.findById(sop.getSopInitiation().getSopId())
//                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));
//
//        // Check if the SOP has been submitted
//        if (sop.getStatus() != SOPStatus.SUBMITTED) {
//            throw new IllegalStateException("Cannot review an SOP that has not been submitted");
//        }
//
//        // Fetch the reviewer User object
//        User reviewerUser = userRepository.findById(reviewerId)
//                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
//
//        // Log the approval pipeline reviewers and current reviewerId for debugging
//        log.info("Approval pipeline reviewers: {}", sopInitiation.getApprovalPipeline().getReviewers());
//        log.info("Current reviewerId: {}", reviewerId);
//
//        // Verify that the reviewer is in the approval pipeline
//        if (sopInitiation.getApprovalPipeline().getReviewers().stream()
//                .noneMatch(r -> r.getId().equals(reviewerId))) {
//            throw new RuntimeException("You are not assigned to review this SOP");
//        }
//
//        // Check if the reviewer has already reviewed this SOP
//        if (sop.getReviews().stream().anyMatch(review -> review.getId().equals(reviewerId))) {
//            throw new RuntimeException("You have already reviewed this SOP");
//        }
//
//        // Create and add the new review
//        Review newReview = new Review();
//        newReview.setReviewerId(reviewerUser);
//        newReview.setConfirmed(isConfirmed);
//        if (sop.getReviews() == null) {
//            sop.setReviews(new ArrayList<>());
//        }
//        sop.getReviews().add(reviewerUser);
//        sop.setUpdatedAt(LocalDateTime.now());
//
//        // Handle status updates based on the review result
//        if (!isConfirmed) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.REJECTED);
//        } else if (allReviewersConfirmed(sopInitiation, sop)) {
//            sopCreationService.updateSOPStatus(sopId, SOPStatus.REVIEW);
//        }
//
//        // Save the updated SOPCreation
//        sopRepository.save(sop);
//
//        // External call to save detailed review (comment, status) in the other service
//        // externalReviewService.logReview(sopId, reviewerId, isConfirmed, comment);
//
//        log.info("SOP reviewed: id={}, title={}, isConfirmed={}", sop.getId(), sop.getTitle(), isConfirmed);
//
//        return sop;
//    }
//
//    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
//        // Get the list of reviewer IDs in the approval pipeline from SOPInitiation
//        List<String> pipelineReviewerIds = sopInitiation.getApprovalPipeline()
//                .getReviewers()
//                .stream()
//                .map(User::getId)  // Extracting reviewer IDs
//                .collect(Collectors.toList());
//
//        // Get the list of confirmed reviewer IDs from SOPCreation's reviews
//        List<String> confirmedReviewerIds = sop.getReviews()
//                .stream()
//                .filter(Review::isConfirmed) // Only confirmed reviews
//                .map(review -> review.getId())  // Extract reviewer IDs
//                .distinct()
//                .collect(Collectors.toList());
//
//        // Compare the two lists: check if all reviewers in the pipeline have confirmed
//        return confirmedReviewerIds.containsAll(pipelineReviewerIds)
//                && pipelineReviewerIds.size() == confirmedReviewerIds.size();
//    }
//}
//
////
////package com.team.sop_management_service.service;
////
////import com.team.sop_management_service.authenticationService.UserDto;
////import com.team.sop_management_service.enums.SOPStatus;
////import com.team.sop_management_service.models.*;
////        import com.team.sop_management_service.repository.ReveiwerReppository;
////import com.team.sop_management_service.repository.SOPCreationRepository;
////import com.team.sop_management_service.repository.SOPInitiationRepository;
////import com.team.sop_management_service.error.SOPNotFoundException;
////import com.team.sop_management_service.config.NotificationService;
////import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////import org.springframework.http.ResponseEntity;
////
////import java.time.LocalDateTime;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.stream.Collectors;
////
////@Service
////@Slf4j
////public class ReviewService {
////
////    private final SOPCreationRepository sopRepository;
////    private final SOPInitiationRepository sopInitiationRepository;
////    private final NotificationService notificationService;
////    private final SOPCreationService sopCreationService;
////    private final ReveiwerReppository reveiwerReppository;
////    private final AuthenticationServiceClient authenticationServiceClient;
////
////    @Autowired
////    public ReviewService(SOPCreationRepository sopRepository,
////                         SOPInitiationRepository sopInitiationRepository,
////                         NotificationService notificationService,
////                         SOPCreationService sopCreationService,
////                         ReveiwerReppository reveiwerReppository,
////                         AuthenticationServiceClient authenticationServiceClient) {
////        this.sopRepository = sopRepository;
////        this.sopInitiationRepository = sopInitiationRepository;
////        this.notificationService = notificationService;
////        this.sopCreationService = sopCreationService;
////        this.reveiwerReppository = reveiwerReppository;
////        this.authenticationServiceClient = authenticationServiceClient;
////    }
////
////    @Transactional
////    public SOPCreation reviewSOP(String sopId, String reviewerId, boolean isConfirmed) {
////        // Fetch the SOPCreation and SOPInitiation
////        SOPCreation sop = sopRepository.findById(sopId)
////                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
////        SOPInitiation sopInitiation = sopInitiationRepository.findById(sop.getSopInitiation().getSopId())
////                .orElseThrow(() -> new RuntimeException("SOP initiation not found"));
////
////        // Check if the SOP has been submitted
////        if (sop.getStatus() != SOPStatus.SUBMITTED) {
////            throw new IllegalStateException("Cannot review an SOP that has not been submitted");
////        }
////
////        // Fetch the reviewer User object from the authentication service
////        ResponseEntity<UserDto> reviewerResponse = authenticationServiceClient.getUserById(reviewerId);
////        if (!reviewerResponse.getStatusCode().is2xxSuccessful() || reviewerResponse.getBody() == null) {
////            throw new RuntimeException("Failed to fetch reviewer information");
////        }
////        UserDto reviewerUser = reviewerResponse.getBody();
////
////        // Log the approval pipeline reviewers and current reviewerId for debugging
////        log.info("Approval pipeline reviewers: {}", sopInitiation.getApprovalPipeline().getReviewers());
////        log.info("Current reviewerId: {}", reviewerId);
////
////        // Verify that the reviewer is in the approval pipeline
////        if (sopInitiation.getApprovalPipeline().getReviewers().stream()
////                .noneMatch(r -> r.getId().equals(reviewerId))) {
////            throw new RuntimeException("You are not assigned to review this SOP");
////        }
////
////        // Check if the reviewer has already reviewed this SOP
////        if (sop.getReviews().stream().anyMatch(review -> review.getId().equals(reviewerId))) {
////            throw new RuntimeException("You have already reviewed this SOP");
////        }
////
////        // Create and add the new review
////        Review newReview = new Review();
////        newReview.setReviewerId(reviewerUser.getId());
////        newReview.setConfirmed(isConfirmed);
////        if (sop.getReviews() == null) {
////            sop.setReviews(new ArrayList<>());
////        }
////        sop.getReviews().add(newReview);
////        sop.setUpdatedAt(LocalDateTime.now());
////
////        // Handle status updates based on the review result
////        if (!isConfirmed) {
////            sopCreationService.updateSOPStatus(sopId, SOPStatus.REJECTED);
////        } else if (allReviewersConfirmed(sopInitiation, sop)) {
////            sopCreationService.updateSOPStatus(sopId, SOPStatus.REVIEW);
////        }
////
////        // Save the updated SOPCreation
////        sopRepository.save(sop);
////
////        log.info("SOP reviewed: id={}, title={}, isConfirmed={}", sop.getId(), sop.getTitle(), isConfirmed);
////
////        return sop;
////    }
////
////    private boolean allReviewersConfirmed(SOPInitiation sopInitiation, SOPCreation sop) {
////        // Get the list of reviewer IDs in the approval pipeline from SOPInitiation
////        List<String> pipelineReviewerIds = sopInitiation.getApprovalPipeline()
////                .getReviewers()
////                .stream()
////                .map(User::getId)
////                .collect(Collectors.toList());
////
////        // Get the list of confirmed reviewer IDs from SOPCreation's reviews
////        List<String> confirmedReviewerIds = sop.getReviews()
////                .stream()
////                .filter(Review::isConfirmed)
////                .map(Review::getReviewerId)
////                .distinct()
////                .collect(Collectors.toList());
////
////        // Compare the two lists: check if all reviewers in the pipeline have confirmed
////        return confirmedReviewerIds.containsAll(pipelineReviewerIds)
////                && pipelineReviewerIds.size() == confirmedReviewerIds.size();
////    }
////}
