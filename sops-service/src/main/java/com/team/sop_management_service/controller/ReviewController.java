package com.team.sop_management_service.controller;

import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.Review;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sopReview")
public class ReviewController {

    private final ReviewService sopService;

    @Autowired
    public ReviewController(ReviewService sopService) {
        this.sopService = sopService;
    }


    @PostMapping("/{sopId}/review")
    public ResponseEntity<?> reviewSOP(
            @PathVariable String sopId,
            @RequestParam String reviewerId,
            @RequestParam boolean isConfirmed
    ) {
        try {
            // Call the service to review the SOP
            SOPCreation reviewedSOP = sopService.reviewSOP(sopId, reviewerId, isConfirmed);

            // Filter the confirmed reviewers from the SOP
            List<User> confirmedReviewers = reviewedSOP.getReviews().stream()
                    .filter(Review::isConfirmed) // Assuming User has an isConfirmed() method
                    .collect(Collectors.toList());

            // Return SOPCreation with confirmed reviewers
            reviewedSOP.setReviews(confirmedReviewers); // Set only confirmed reviewers in SOPCreation

            return ResponseEntity.ok(reviewedSOP);
        } catch (SOPNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("SOP not found: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error reviewing SOP: " + e.getMessage());
        }
    }

    }
