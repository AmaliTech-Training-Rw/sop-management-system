package com.team.sop_management_service.controller;

import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.SOPCreationService;
import com.team.sop_management_service.exceptions.SOPNotFoundException;
import com.team.sop_management_service.enums.SOPStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sop")
@RequiredArgsConstructor
@Tag(name = "SOP Management", description = "Endpoints for creating, updating, and reviewing SOPs")
public class SOPCreationController {
    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);


    private final SOPCreationService sopCreationService;

    @PostMapping("/create")
    @Operation(summary = "Create a new SOP",
            description = "Create a new SOP document. Attach files and link it to an existing SOP Initiation.")
    public ResponseEntity<SOPCreation> createSOP(@Valid @RequestBody SOPCreationDTO sopCreationDTO) {
        SOPCreation createdSOP = sopCreationService.createSOP(sopCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSOP);
    }

    @PutMapping("/submit/{id}")
    @Operation(summary = "Submit SOP for Review",
            description = "Submit an existing SOP for review. Only SOPs in draft status can be submitted.")
    public ResponseEntity<SOPCreation> submitSOPForReview(@PathVariable("id") String id) throws SOPNotFoundException {
        logger.warn("Only the current version can be submitted for review.");
        SOPCreation submittedSOP = sopCreationService.submitSOPForReview(id);
        return ResponseEntity.ok(submittedSOP);
    }
    // Undo the submission of an SOP
    @PostMapping("/{id}/undo-submission")
    public ResponseEntity<SOPCreation> undoSOPSubmission(@PathVariable String id) {
        try {
            SOPCreation revertedSOP = sopCreationService.undoSubmission(id);
            logger.info("SOP submission undone for ID: {}", id);
            return ResponseEntity.ok(revertedSOP);
        } catch (SOPNotFoundException e) {
            logger.error("SOP not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            logger.error("Error undoing SOP submission: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SOP by ID",
            description = "Retrieve an SOP document by its ID.")
    public ResponseEntity<SOPCreation> getSOPCreationById(@PathVariable("id") String id) throws SOPNotFoundException {
        SOPCreation sopCreation = sopCreationService.getSOPById(id);
        return ResponseEntity.ok(sopCreation);
    }

    @GetMapping("/all")
    @Operation(summary = "Get All SOPs",
            description = "Retrieve a list of all SOP documents with pagination support.")
    public ResponseEntity<Page<SOPCreation>> getAllSOPs(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Page<SOPCreation> allSOPs = sopCreationService.getAllSOPsPaged(pageNumber, pageSize, sortBy, direction);
        return ResponseEntity.ok(allSOPs);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update SOP",
            description = "Update an existing SOP document by its ID.")
    public ResponseEntity<SOPCreation> updateSOP(
            @PathVariable("id") String id,
            @RequestBody SOPCreationDTO updatedSOPDTO) throws SOPNotFoundException {
        SOPCreation sopCreation = sopCreationService.updateSOP(id, updatedSOPDTO);
        return ResponseEntity.ok(sopCreation);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete SOP",
            description = "Delete an SOP document by its ID.")
    public ResponseEntity<String> deleteSOP(@PathVariable("id") String id) throws SOPNotFoundException {
        sopCreationService.deleteSOP(id);
        return ResponseEntity.ok("SOP with ID " + id + " has been deleted.");
    }

    @GetMapping("/{sopReferenceId}/current")
    @Operation(summary = "Get current version of an SOP", description = "Retrieves the current version of a Standard Operating Procedure")
    @ApiResponse(responseCode = "200", description = "Current version retrieved successfully")
    public ResponseEntity<SOPCreation> getCurrentVersion(@PathVariable String sopReferenceId) throws SOPNotFoundException {
        SOPCreation currentVersion = sopCreationService.getCurrentVersion(sopReferenceId);
        return ResponseEntity.ok(currentVersion);
    }

    @GetMapping("/{sopReferenceId}/versions")
    @Operation(summary = "Get all versions of an SOP", description = "Retrieves all versions of a Standard Operating Procedure")
    @ApiResponse(responseCode = "200", description = "All versions retrieved successfully")
    public ResponseEntity<List<SOPCreation>> getAllVersions(@PathVariable String sopReferenceId) {
        List<SOPCreation> versions = sopCreationService.getAllVersions(sopReferenceId);
        return ResponseEntity.ok(versions);
    }

    @PostMapping("/{sopReferenceId}/revert/{version}")
    @Operation(summary = "Revert to a specific version", description = "Reverts a Standard Operating Procedure to a specific version")
    @ApiResponse(responseCode = "200", description = "SOP reverted successfully")
    public ResponseEntity<SOPCreation> revertToVersion(@PathVariable String sopReferenceId, @PathVariable int version) throws SOPNotFoundException {
        SOPCreation revertedSOP = sopCreationService.revertToVersion(sopReferenceId, version);
        return ResponseEntity.ok(revertedSOP);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update SOP status", description = "Updates the status of a Standard Operating Procedure")
    @ApiResponse(responseCode = "200", description = "SOP status updated successfully")
    public ResponseEntity<String> updateSOPStatus(
            @PathVariable("id") String id,
            @RequestParam SOPStatus newStatus) {
        sopCreationService.updateSOPStatus(id, newStatus);
        return ResponseEntity.ok("SOP status updated successfully");
    }
}