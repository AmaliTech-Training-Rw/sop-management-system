package com.team.sop_management_service.controller;

import com.team.sop_management_service.error.InvalidSOPException;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.service.SOPService;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.enums.SOPStatus;
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

    private final SOPService sopService;

    @Autowired
    public SOPController(SOPService sopService) {
        this.sopService = sopService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateSOP(@RequestBody SOP sop) {
        try {
            SOP initiatedSOP = sopService.initiateSOP(sop);
            return ResponseEntity.ok(initiatedSOP);
        } catch (InvalidSOPException e) {
            logger.error("Invalid SOP data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while initiating SOP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping
    public ResponseEntity<?> saveSOP(@RequestBody SOP sop) {
        try {
            SOP savedSOP = sopService.saveSOP(sop);
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
            List<SOP> sops = sopService.getAllSOPs();
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve all SOPs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSOPById(@PathVariable String id) {
        try {
            SOP sop = sopService.getSOPById(id);
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
            List<SOP> sops = sopService.getSOPsByVisibility(visibility);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by visibility: {}", visibility, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by visibility");
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getSOPsByStatus(@PathVariable SOPStatus status) {
        try {
            List<SOP> sops = sopService.getSOPsByStatus(status);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by status");
        }
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getSOPsByAuthor(@PathVariable String authorId) {
        try {
            List<SOP> sops = sopService.getSOPsByAuthor(authorId);
            return ResponseEntity.ok(sops);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by author id: {}", authorId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve SOPs by author");
        }
    }
}