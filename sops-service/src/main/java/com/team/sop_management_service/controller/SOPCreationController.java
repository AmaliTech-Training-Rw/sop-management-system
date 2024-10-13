//package com.team.sop_management_service.controller;
//
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.service.SOPCreationService;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/sop")
//@RequiredArgsConstructor
//@Tag(name = "SOP Management", description = "Endpoints for creating, updating, and reviewing SOPs")
//public class SOPCreationController {
//
//    private final SOPCreationService sopCreationService;
//
//    @PostMapping("/create")
//    @Operation(summary = "Create a new SOP",
//            description = "Create a new SOP document. Attach files and link it to an existing SOP Initiation.")
//    public ResponseEntity<SOPCreation> createSOP(@Valid @RequestBody SOPCreation sopCreation)
//            throws SOPNotFoundException {
//
//        // Log incoming SOPCreation for debugging
//        System.out.println("Received SOPCreation request: " + sopCreation);
//
//        // Call the service to create the SOP
//        SOPCreation createdSOP = sopCreationService.createSOP(sopCreation);
//
//        // Return the created SOP with HTTP status 201 Created
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdSOP);
//    }
//
//    @PutMapping("/submit/{id}")
//    @Operation(summary = "Submit SOP for Review",
//            description = "Submit an existing SOP for review. Only SOPs in draft status can be submitted.")
//    public ResponseEntity<SOPCreation> submitSOPForReview(
//            @PathVariable("id") String id,
//            @RequestParam("updatedBy") String updatedBy) throws SOPNotFoundException {
//
//        SOPCreation submittedSOP = sopCreationService.submitSOPForReview(id);
//        return ResponseEntity.ok(submittedSOP);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get SOP by ID",
//            description = "Retrieve an SOP document by its ID.")
//    public ResponseEntity<SOPCreation> getSOPCreationById(
//            @PathVariable("id") String id) throws SOPNotFoundException {
//
//        SOPCreation sopCreation = sopCreationService.getSOPById(id);
//        return ResponseEntity.ok(sopCreation);
//    }
//
////    @GetMapping("/all")
////    @Operation(summary = "Get All SOPs",
////            description = "Retrieve a list of all SOP documents with pagination support.")
////    public ResponseEntity<Page<SOPCreation>> getAllSOPs(Pageable pageable) {
////        Page<SOPCreation> allSOPs = sopCreationService.getAllVersionsPaged(pageable);
////        return ResponseEntity.ok(allSOPs);
////    }
//@GetMapping("/all")
//@Operation(summary = "Get All SOPs",
//        description = "Retrieve a list of all SOP documents with pagination support.")
//public ResponseEntity<List<SOPCreation>> getAllSOPs() {
//    List<SOPCreation> allSOPs = sopCreationService.getAllVersionsPaged();
//    return ResponseEntity.ok(allSOPs);
//}
//
//    @PutMapping("/update/{id}")
//    @Operation(summary = "Update SOP",
//            description = "Update an existing SOP document by its ID.")
//    public ResponseEntity<SOPCreation> updateSOP(
//            @PathVariable("id") String id,
//            @RequestBody SOPCreation updatedSOP) throws SOPNotFoundException {
//
//        SOPCreation sopCreation = sopCreationService.updateSOP(id, updatedSOP);
//        return ResponseEntity.ok(sopCreation);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    @Operation(summary = "Delete SOP",
//            description = "Delete an SOP document by its ID.")
//    public ResponseEntity<String> deleteSOP(@PathVariable("id") String id) throws SOPNotFoundException {
//        sopCreationService.deleteSOP(id);
//        return ResponseEntity.ok("SOP with ID " + id + " has been deleted.");
//    }
//
//    @GetMapping("/cached/{id}")
//    @Operation(summary = "Get Cached SOP by ID",
//            description = "Retrieve an SOP document by its ID from the cache.")
//    public ResponseEntity<SOPCreation> getCachedSOPById(
//            @PathVariable("id") String id) throws SOPNotFoundException {
//
//        SOPCreation cachedSOP = sopCreationService.getCachedSOPById(id);
//        return ResponseEntity.ok(cachedSOP);
//    }
//    @GetMapping("/{sopReferenceId}/current")
//    @Operation(summary = "Get current version of an SOP", description = "Retrieves the current version of a Standard Operating Procedure")
//    @ApiResponse(responseCode = "200", description = "Current version retrieved successfully")
//    public ResponseEntity<SOPCreation> getCurrentVersion(@PathVariable String sopReferenceId) {
//        SOPCreation currentVersion = sopCreationService.getCurrentVersion(sopReferenceId);
//        return ResponseEntity.ok(currentVersion);
//    }
//
//    @GetMapping("/{sopReferenceId}/versions")
//    @Operation(summary = "Get all versions of an SOP", description = "Retrieves all versions of a Standard Operating Procedure")
//    @ApiResponse(responseCode = "200", description = "All versions retrieved successfully")
//    public ResponseEntity<List<SOPCreation>> getAllVersions(@PathVariable String sopReferenceId) {
//        List<SOPCreation> versions = sopCreationService.getAllVersions(sopReferenceId);
//        return ResponseEntity.ok(versions);
//    }
//
//    @PostMapping("/{sopReferenceId}/revert/{version}")
//    @Operation(summary = "Revert to a specific version", description = "Reverts a Standard Operating Procedure to a specific version")
//    @ApiResponse(responseCode = "200", description = "SOP reverted successfully")
//    public ResponseEntity<SOPCreation> revertToVersion(@PathVariable String sopReferenceId, @PathVariable int version) {
//        SOPCreation revertedSOP = sopCreationService.revertToVersion(sopReferenceId, version);
//        return ResponseEntity.ok(revertedSOP);
//    }
//}
//


package com.team.sop_management_service.controller;

import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.SOPCreationService;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.enums.SOPStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sop")
@RequiredArgsConstructor
@Tag(name = "SOP Management", description = "Endpoints for creating, updating, and reviewing SOPs")
public class SOPCreationController {

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
        SOPCreation submittedSOP = sopCreationService.submitSOPForReview(id);
        return ResponseEntity.ok(submittedSOP);
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

    @GetMapping("/cached/{id}")
    @Operation(summary = "Get Cached SOP by ID",
            description = "Retrieve an SOP document by its ID from the cache.")
    public ResponseEntity<SOPCreation> getCachedSOPById(@PathVariable("id") String id) throws SOPNotFoundException {
        SOPCreation cachedSOP = sopCreationService.getCachedSOPById(id);
        return ResponseEntity.ok(cachedSOP);
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

    @GetMapping("/{sopReferenceId}/versions/paged")
    @Operation(summary = "Get all versions of an SOP (paged)", description = "Retrieves all versions of a Standard Operating Procedure with pagination")
    @ApiResponse(responseCode = "200", description = "Paged versions retrieved successfully")
    public ResponseEntity<Page<SOPCreation>> getAllVersionsPaged(
            @PathVariable String sopReferenceId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SOPCreation> versions = sopCreationService.getAllVersionsPaged(sopReferenceId, pageNumber, pageSize);
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