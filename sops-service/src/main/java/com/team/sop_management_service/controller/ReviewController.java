package com.team.sop_management_service.controller;

import com.team.sop_management_service.models.Review;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{sopId}")
    public ResponseEntity<SOPCreation> reviewSOP(
            @PathVariable String sopId,
            @RequestParam int reviewerId,
            @RequestParam boolean isConfirmed,
            @RequestParam(required = false) String comment) {

        SOPCreation reviewedSOP = reviewService.reviewSOP(sopId, reviewerId, isConfirmed, comment);
        return ResponseEntity.ok(reviewedSOP);
    }

//    // You can add more endpoints here as needed, for example:
//
//    @GetMapping("/{sopId}")
//    public ResponseEntity<List<Review>> getReviewsForSOP(@PathVariable String sopId) {
//        // Implement this method in your ReviewService
//        List<Review> reviews = reviewService.getReviewsForSOP(sopId);
//        return ResponseEntity.ok(reviews);
//    }
//
//    @GetMapping("/reviewer/{reviewerId}")
//    public ResponseEntity<List<SOPCreation>> getSOPsForReviewer(@PathVariable int reviewerId) {
//        // Implement this method in your ReviewService
//        List<SOPCreation> sops = reviewService.getSOPsForReviewer(reviewerId);
//        return ResponseEntity.ok(sops);
//    }
}