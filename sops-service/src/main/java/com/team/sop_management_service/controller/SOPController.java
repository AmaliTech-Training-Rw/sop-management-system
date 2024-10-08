package com.team.sop_management_service.controller;

import com.team.sop_management_service.error.SOPInitiationException;
import com.team.sop_management_service.error.SOPRetrievalException;
import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.service.SOPService;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.models.ApprovalPipeline;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sop")
public class SOPController {

    private final SOPService sopService;
    private static final Logger log = LoggerFactory.getLogger(SOPController.class);


    @Autowired
    public SOPController(SOPService sopService) {
        this.sopService = sopService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<SOP> initiateSOP(
            @RequestBody SOP sops,
            @RequestParam(value = "hodDepartmentId", required = false)  String hodDepartmentId) {

        try {
            SOP sop = sopService.initiateSOP(sops, hodDepartmentId);
            return new ResponseEntity<>(sop, HttpStatus.CREATED);
        } catch (SOPInitiationException e) {
            // Log specific SOP initiation issue
            log.error("SOP Initiation failed: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log generic error with stack trace
            log.error("Internal server error: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<SOP>> getAllSOPs() {
        try {
            List<SOP> sops = sopService.getAllSOPs();
            return new ResponseEntity<>(sops, HttpStatus.OK);
        } catch (SOPRetrievalException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
