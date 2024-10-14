package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.dto.SOPInitiationDTO;
import com.team.sop_management_service.exceptions.InvalidSOPException;
import com.team.sop_management_service.exceptions.SOPNotFoundException;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.models.ApprovalPipeline;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import com.team.sop_management_service.enums.Visibility;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SOPInitiationService {
    private static final Logger logger = LoggerFactory.getLogger(SOPInitiationService.class);

    private final SOPInitiationRepository sopRepository;
    private final AuthenticationServiceClient authenticationServiceClient;
    private final NotificationService notificationService;

    @Autowired
    public SOPInitiationService(SOPInitiationRepository sopRepository, AuthenticationServiceClient authenticationServiceClient, NotificationService notificationService) {
        this.sopRepository = sopRepository;
        this.authenticationServiceClient = authenticationServiceClient;
        this.notificationService = notificationService;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public SOPInitiationDTO initiateSOP(SOPInitiationDTO sopDTO) {
        try {
            logger.info("Initiating SOP: {}", sopDTO.getTitle());

            validateUser(sopDTO.getApprovalPipeline().getApprover(), "Approver");
            validateUser(sopDTO.getApprovalPipeline().getAuthor(), "Author");
            sopDTO.getApprovalPipeline().getReviewers().forEach(reviewer -> validateUser(reviewer, "Reviewer"));

            validateSOP(sopDTO);

            SOPInitiation sopEntity = SOPInitiation.builder()
                    .title(sopDTO.getTitle())
                    .visibility(sopDTO.getVisibility())
                    .approvalPipeline(sopDTO.getApprovalPipeline())
                    .build();

            SOPInitiation savedSOP = sopRepository.save(sopEntity);
           // notificationService.notifyAuthor(savedSOP);


            return SOPInitiationDTO.builder()
                    .sopId(savedSOP.getSopId())
                    .title(savedSOP.getTitle())
                    .visibility(savedSOP.getVisibility())
                    .approvalPipeline(savedSOP.getApprovalPipeline())
                    .build();
        } catch (InvalidSOPException e) {
            logger.error("Failed to initiate SOP: {}", e.getMessage());
            throw e;
        } catch (FeignException e) {
            logger.error("Failed to validate user through authentication service: {}", e.getMessage());
            throw new RuntimeException("Failed to validate user", e);
        } catch (Exception e) {
            logger.error("Unexpected error while initiating SOP", e);
            throw new RuntimeException("Failed to initiate SOP", e);
        }
    }

    public List<SOPInitiationDTO> getAllSOPs() {
        logger.info("Fetching all SOPs");
        try {
            return sopRepository.findAll().stream()
                    .map(sop -> SOPInitiationDTO.builder()
                            .sopId(sop.getSopId())
                            .title(sop.getTitle())
                            .visibility(sop.getVisibility())
                            .approvalPipeline(sop.getApprovalPipeline())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all SOPs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch all SOPs", e);
        }
    }

    public List<SOPInitiationDTO> getSOPsByVisibility(Visibility visibility) {
        logger.info("Fetching SOPs with visibility: {}", visibility);
        try {
            return sopRepository.findByVisibility(visibility).stream()
                    .map(sop -> SOPInitiationDTO.builder()
                            .sopId(sop.getSopId())
                            .title(sop.getTitle())
                            .visibility(sop.getVisibility())
                            .approvalPipeline(sop.getApprovalPipeline())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching SOPs with visibility {}: {}", visibility, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SOPs by visibility", e);
        }
    }

    // Method to validate a user based on their ID and role
    private void validateUser(int userId, String role) {
        try {
            ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("{} with ID {} is valid", role, userId);
            } else {
                throw new InvalidSOPException(role + " with ID " + userId + " is not valid");
            }
        } catch (FeignException.NotFound e) {
            throw new InvalidSOPException(role + " with ID " + userId + " does not exist");
        } catch (FeignException e) {
            logger.error("Error validating {} with ID {}: {}", role, userId, e.getMessage());
            throw new RuntimeException("Error validating " + role, e);
        }
    }


    // Validate SOP details
    private void validateSOP(SOPInitiationDTO sop) {
        if (sop.getTitle() == null || sop.getTitle().isEmpty()) {
            throw new InvalidSOPException("SOP title cannot be empty");
        }
        if (sop.getVisibility() == null) {
            throw new InvalidSOPException("SOP visibility must be specified");
        }
        validateApprovalPipeline(sop.getApprovalPipeline(), sop.getVisibility()); // Validate approval pipeline
    }

    private void validateApprovalPipeline(ApprovalPipeline pipeline, Visibility visibility) {
        if (pipeline == null || pipeline.getAuthor() == 0 || pipeline.getApprover() == 0 ||
                pipeline.getReviewers() == null || pipeline.getReviewers().isEmpty()) {
            throw new InvalidSOPException("Invalid approval pipeline. Author, approver, and reviewers must be defined.");
        }

        int hod = pipeline.getApprover();

        // For department visibility, ensure all are from the same department
        if (visibility == Visibility.DEPARTMENT) {
            if (!isSameDepartment(hod, pipeline.getAuthor()) ||
                    !pipeline.getReviewers().stream().allMatch(reviewer -> isSameDepartment(hod, reviewer))) {
                throw new InvalidSOPException("For department visibility, all staff must be from the same department.");
            }
        }

        // Approver must be HoD or from the same department
        if (!isValidApprover(pipeline.getApprover(), hod)) {
            throw new InvalidSOPException("Approver must be the HoD or from the same department as the HoD.");
        }

        logger.info("Approval pipeline validated successfully");
    }

    private boolean isSameDepartment(int user1Id, int user2Id) {
        ResponseEntity<UserDto> user1Response = authenticationServiceClient.getUserById(user1Id);
        ResponseEntity<UserDto> user2Response = authenticationServiceClient.getUserById(user2Id);

        if (user1Response.getStatusCode().is2xxSuccessful() && user2Response.getStatusCode().is2xxSuccessful()) {
            UserDto user1 = user1Response.getBody();
            UserDto user2 = user2Response.getBody();

            if (user1 != null && user2 != null) {
                String department1 = String.valueOf(user1.getDepartment());
                String department2 = String.valueOf(user2.getDepartment());
                return department1 != null && department1.equals(department2);
            }
        }

        return false;
    }

    private boolean isValidApprover(int approver, int hod) {
        return approver == hod || isSameDepartment(approver, hod);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSOP(String id) {
        try {
            logger.info("Deleting SOP with id: {}", id);
            sopRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Failed to delete SOP with id: {}", id, e);
            throw new RuntimeException("Failed to delete SOP", e);
        }
    }
    public SOPInitiation getSOPById(String id) throws SOPNotFoundException {
        logger.info("Fetching SOP with ID: {}", id);

        try {
            if (id == null) {
                throw new IllegalArgumentException("SOP ID cannot be null");
            }
            return sopRepository.findById(id)
                    .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
        } catch (SOPNotFoundException e) {
            logger.error("SOP not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching SOP with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SOP", e);
        }
    }

}