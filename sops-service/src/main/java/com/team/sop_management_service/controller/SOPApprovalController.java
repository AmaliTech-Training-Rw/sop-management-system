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
        SOPCreation approvedSOP = approvalService.approveOrRejectSOP(sopId, approverId);
        return ResponseEntity.ok(approvedSOP);
    }

    @PostMapping("/{sopId}/reject")
    public ResponseEntity<SOPCreation> rejectSOP(
            @PathVariable String sopId,
            @RequestParam int approverId) {
        SOPCreation rejectedSOP = approvalService.approveOrRejectSOP(sopId, approverId);
        return ResponseEntity.ok(rejectedSOP);
    }
}