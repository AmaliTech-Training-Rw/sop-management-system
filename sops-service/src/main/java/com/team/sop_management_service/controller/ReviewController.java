package com.team.sop_management_service.controller;

import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sops")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{sopId}/review")
    public ResponseEntity<SOPCreation> reviewSOP(
            @PathVariable String sopId,
            @RequestParam String reviewerId,
            @RequestParam boolean isConfirmed,
            @RequestParam(required = false) String comment) {
        try {
            SOPCreation updatedSOP = reviewService.reviewSOP(sopId, reviewerId, isConfirmed, comment);
            return ResponseEntity.ok(updatedSOP);
        } catch (RuntimeException e) {
// Return an error response with a null SOPCreation
            return ResponseEntity.badRequest().body(null); // Retur        }
        }}
}
