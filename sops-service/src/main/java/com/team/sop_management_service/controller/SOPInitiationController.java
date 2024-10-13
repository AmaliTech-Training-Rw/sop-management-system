package com.team.sop_management_service.controller;

import com.mongodb.MongoException;
import com.team.sop_management_service.dto.SOPInitiationDTO;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.service.SOPInitiationService;
import com.team.sop_management_service.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sops")
public class SOPInitiationController {
    private static final Logger logger = LoggerFactory.getLogger(SOPInitiationController.class);

    private final SOPInitiationService sopService;

    @Autowired
    public SOPInitiationController(SOPInitiationService sopService) {
        this.sopService = sopService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateSOP(@Valid @RequestBody SOPInitiationDTO sop) {
        try {
            SOPInitiationDTO initiatedSOP = sopService.initiateSOP(sop);
            return ResponseEntity.ok(initiatedSOP);
        } catch (MongoException e) {
            logger.error("MongoDB error while saving SOP: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate SOP due to database error");
        } catch (Exception e) {
            logger.error("Unexpected error while initiating SOP: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate SOP");
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
            List<SOPInitiationDTO> sops = sopService.getAllSOPs();
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
            List<SOPInitiationDTO> sops = sopService.getSOPsByVisibility(visibility);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by visibility: {}", visibility, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by visibility");
        }
    }
//
//    @GetMapping("/author/{authorId}")
//    public ResponseEntity<?> getSOPsByAuthor(@PathVariable int authorId) {
//        try {
//            List<SOPInitiationDTO> sops = sopService.getSOPsByAuthor(authorId);
//            return ResponseEntity.ok(sops);
//        } catch (Exception e) {
//            logger.error("Failed to retrieve SOPs by author id: {}", authorId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by author");
//        }
//    }
}