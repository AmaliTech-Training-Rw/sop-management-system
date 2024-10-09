//package com.team.sop_management_service.controller;
//
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.service.SOPCreationService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/sops")
//@RequiredArgsConstructor
//public class SOPCreationController {
//    private static final Logger logger = LoggerFactory.getLogger(SOPCreationController.class);
//    private final SOPCreationService sopCreationService;
//
//    @PostMapping
//    public ResponseEntity<SOPCreation> createSOP(@Valid @RequestPart("sop") SOPCreation sopCreation,
//                                                 @RequestPart(value = "files", required = false) List<MultipartFile> files) {
//        logger.info("Received request to create new SOP: {}", sopCreation.getTitle());
//        try {
//            SOPCreation createdSOP = sopCreationService.createSOP(sopCreation, files);
//            logger.info("SOP created successfully with ID: {}", createdSOP.getId());
//            return new ResponseEntity<>(createdSOP, HttpStatus.CREATED);
//        } catch (RuntimeException e) {
//            logger.error("Failed to create SOP due to file storage error", e);
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SOPCreation> getSOPById(@PathVariable String id) {
//        logger.info("Received request to get SOP with ID: {}", id);
//        try {
//            SOPCreation sop = sopCreationService.getSOPById(id);
//            return ResponseEntity.ok(sop);
//            } catch (RuntimeException e) {
//            logger.warn("SOP not found with ID: {}", id);
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<List<SOPCreation>> getAllSOPs() {
//        logger.info("Received request to get all SOPs");
//        List<SOPCreation> sops = sopCreationService.getAllSOPs();
//        return ResponseEntity.ok(sops);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SOPCreation> updateSOP(@PathVariable String id, @Valid @RequestBody SOPCreation sopCreation) {
//        logger.info("Received request to update SOP with ID: {}", id);
//        try {
//            SOPCreation updatedSOP = sopCreationService.updateSOP(id, sopCreation);
//            logger.info("SOP updated successfully with ID: {}", updatedSOP.getId());
//            return ResponseEntity.ok(updatedSOP);
//        } catch (SOPNotFoundException e) {
//            logger.warn("Failed to update SOP. SOP not found with ID: {}", id);
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSOP(@PathVariable String id) {
//        logger.info("Received request to delete SOP with ID: {}", id);
//        try {
//            sopCreationService.deleteSOP(id);
//            logger.info("SOP deleted successfully with ID: {}", id);
//            return ResponseEntity.noContent().build();
//        } catch (SOPNotFoundException e) {
//            logger.warn("Failed to delete SOP. SOP not found with ID: {}", id);
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/reference/{sopReferenceId}")
//    public ResponseEntity<SOPCreation> getSOPByReferenceId(@PathVariable String sopReferenceId) {
//        logger.info("Received request to get SOP with reference ID: {}", sopReferenceId);
//        try {
//            SOPCreation sop = sopCreationService.getSOPByReferenceId(sopReferenceId);
//            return ResponseEntity.ok(sop);
//        } catch (SOPNotFoundException e) {
//            logger.warn("SOP not found with reference ID: {}", sopReferenceId);
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGeneralException(Exception e) {
//        logger.error("An unexpected error occurred", e);
//        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}


package com.team.sop_management_service.controller;

import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.SOPCreationService;
import com.team.sop_management_service.error.SOPNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<SOPCreation> createSOP(
            @RequestPart("sopCreation") SOPCreation sopCreation,
            @RequestPart("files") List<MultipartFile> files
          ) throws SOPNotFoundException {

        SOPCreation createdSOP = sopCreationService.createSOP(sopCreation, files);
        return ResponseEntity.ok(createdSOP);
    }

    @PutMapping("/submit/{id}")
    @Operation(summary = "Submit SOP for Review",
            description = "Submit an existing SOP for review. Only SOPs in draft status can be submitted.")
    public ResponseEntity<SOPCreation> submitSOPForReview(
            @PathVariable("id") String id,
            @RequestParam("updatedBy") String updatedBy) throws SOPNotFoundException {

        SOPCreation submittedSOP = sopCreationService.submitSOPForReview(id, updatedBy);
        return ResponseEntity.ok(submittedSOP);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SOP by ID",
            description = "Retrieve an SOP document by its ID.")
    public ResponseEntity<SOPCreation> getSOPCreationById(
            @PathVariable("id") String id) throws SOPNotFoundException {

        SOPCreation sopCreation = sopCreationService.getSOPById(id);
        return ResponseEntity.ok(sopCreation);
    }

    @GetMapping("/all")
    @Operation(summary = "Get All SOPs",
            description = "Retrieve a list of all SOP documents.")
    public ResponseEntity<List<SOPCreation>> getAllSOPs() {
        List<SOPCreation> allSOPs = sopCreationService.getAllSOPs();
        return ResponseEntity.ok(allSOPs);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update SOP",
            description = "Update an existing SOP document by its ID.")
    public ResponseEntity<SOPCreation> updateSOP(
            @PathVariable("id") String id,
            @RequestBody SOPCreation updatedSOP) throws SOPNotFoundException {

        SOPCreation sopCreation = sopCreationService.updateSOP(id, updatedSOP);
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
    public ResponseEntity<SOPCreation> getCachedSOPById(
            @PathVariable("id") String id) throws SOPNotFoundException {

        SOPCreation cachedSOP = sopCreationService.getCachedSOPById(id);
        return ResponseEntity.ok(cachedSOP);
    }
}
