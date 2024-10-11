package com.team.sop_management_service.controller;

import com.mongodb.MongoException;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.service.SOPInitiationService;
import com.team.sop_management_service.enums.Visibility;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/sops")
public class SOPController {
    private static final Logger logger = LoggerFactory.getLogger(SOPController.class);

    private final SOPInitiationService sopService;

    @Autowired
    public SOPController(SOPInitiationService sopService) {
        this.sopService = sopService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateSOP(@RequestBody SOPInitiation sop) {
        try {
            SOPInitiation initiatedSOP = sopService.initiateSOP(sop);
            return ResponseEntity.ok(initiatedSOP);
        } catch (MongoException e) {
            // Log the exception with more details
            logger.error("MongoDB error while saving SOP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate SOP due to database error", e);
        } catch (Exception e) {
            // Handle other exceptions
            logger.error("Unexpected error while initiating SOP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate SOP", e);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveSOP(@RequestBody SOPInitiation sop) {
        try {
            SOPInitiation savedSOP = sopService.saveSOP(sop);
            return ResponseEntity.ok(savedSOP);
        } catch (Exception e) {
            logger.error("Failed to save SOP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save SOP");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSOP(@PathVariable String id) {
        try {
            sopService.deleteSOP(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete SOP with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete SOP");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSOPs() {
        try {
            List<SOPInitiation> sops = sopService.getAllSOPs();
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve all SOPs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSOPById(@PathVariable String id) {
        try {
            SOPInitiation sop = sopService.getSOPById(id);
            return ResponseEntity.ok(sop);
        } catch (SOPNotFoundException e) {
            logger.error("SOP not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to retrieve SOP with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOP");
        }
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<?> getSOPsByVisibility(@PathVariable Visibility visibility) {
        try {
            List<SOPInitiation> sops = sopService.getSOPsByVisibility(visibility);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by visibility: {}", visibility, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by visibility");
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getSOPsByAuthor(@PathVariable String authorId) {
        try {
            List<SOPInitiation> sops = sopService.getSOPsByAuthor(authorId);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by author id: {}", authorId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by author");
        }
    }
}