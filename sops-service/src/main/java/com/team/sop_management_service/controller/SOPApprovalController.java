//package com.team.sop_management_service.controller;
//
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.service.ApprovalService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/sops")
//@Slf4j
//public class SOPApprovalController {
//
//    // ApprovalService to handle the business logic
//    private final ApprovalService approvalService;
//
//    @Autowired
//    public SOPApprovalController(ApprovalService approvalService) {
//        this.approvalService = approvalService;
//    }
//   //API endpoint to approve or reject an SOP.
//    @PostMapping("/{sopId}/approve-reject")
//    public ResponseEntity<SOPCreation> approveOrRejectSOP(
//            @PathVariable String sopId,
//            @RequestParam String approverId,
//            @RequestParam boolean isApproved) {
//
//        log.info("Received request to {} SOP with ID: {}", isApproved ? "approve" : "reject", sopId);
//
//        // Call the service to approve or reject the SOP
//        SOPCreation updatedSop = approvalService.approveOrRejectSOP(sopId, approverId, isApproved);
//
//        // Return a response with the updated SOP and an appropriate HTTP status
//        return ResponseEntity.ok(updatedSop);
//    }
//}







package com.team.sop_management_service.controller;

import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sop/approval")
@RequiredArgsConstructor
public class SOPApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/{sopId}/approve")
    public ResponseEntity<SOPCreation> approveSOP(
            @PathVariable String sopId,
            @RequestParam int approverId) {
        SOPCreation approvedSOP = approvalService.approveOrRejectSOP(sopId, approverId, true);
        return ResponseEntity.ok(approvedSOP);
    }

    @PostMapping("/{sopId}/reject")
    public ResponseEntity<SOPCreation> rejectSOP(
            @PathVariable String sopId,
            @RequestParam int approverId) {
        SOPCreation rejectedSOP = approvalService.approveOrRejectSOP(sopId, approverId, false);
        return ResponseEntity.ok(rejectedSOP);
    }
}