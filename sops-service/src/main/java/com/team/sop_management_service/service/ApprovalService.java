package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.exceptions.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalService {

    private final SOPCreationRepository sopRepository;
    private final SOPCreationService sopCreationService;
    private final AuthenticationServiceClient authService;

    @Transactional
    public SOPCreation approveOrRejectSOP(String sopId, int approverId) {
        SOPCreation sop = sopRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));

        if (sop.getStatus() != SOPStatus.REVIEWED) {
            throw new IllegalStateException("Cannot approve/reject an SOP that has not been reviewed");
        }

        UserDto approver = authService.getUserById(approverId).getBody();
        if (approver == null) {
            throw new RuntimeException("Approver not found");
        }

        if (sop.getSopInitiation().getApprovalPipeline().getApprover() != approverId) {
            throw new RuntimeException("You are not assigned to approve this SOP");
        }

        // Set the status to APPROVED and isApproved to true
        SOPStatus newStatus = SOPStatus.APPROVED;
        sop.setStatus(newStatus);
        sop.setApproved(true);

        log.info("SOP approved: id={}, title={}", sop.getId(), sop.getTitle());

        return sopRepository.save(sop);
    }
}
