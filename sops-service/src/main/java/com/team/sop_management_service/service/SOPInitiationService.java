//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
//import com.team.sop_management_service.authenticationService.UserDto;
//import com.team.sop_management_service.config.NotificationService;
//import com.team.sop_management_service.dto.SOPInitiationDTO;
//import com.team.sop_management_service.error.InvalidSOPException;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.SOPInitiation;
//import com.team.sop_management_service.models.ApprovalPipeline;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.enums.Visibility;
//import feign.FeignException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.annotation.Propagation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class SOPInitiationService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPInitiationService.class);
//
//    private final SOPInitiationRepository sopRepository;
//    private final NotificationService notificationService;
//    private final AuthenticationServiceClient authenticationServiceClient;
//
//    @Autowired
//    public SOPInitiationService(SOPInitiationRepository sopRepository, NotificationService notificationService, AuthenticationServiceClient authenticationServiceClient) {
//        this.sopRepository = sopRepository;
//        this.notificationService = notificationService;
//        this.authenticationServiceClient = authenticationServiceClient;
//    }
//
//    @Transactional(propagation = Propagation.REQUIRED)
//    public SOPInitiationDTO initiateSOP(SOPInitiationDTO sopDTO) {
//        try {
//            logger.info("Initiating SOP: {}", sopDTO.getTitle());
//
//            // Validate users involved in the SOP process
//            validateUser(sopDTO.getApprovalPipeline().getAuthor(), "Author");
//            validateUser(sopDTO.getApprovalPipeline().getApprover(), "Approver");
//            sopDTO.getApprovalPipeline().getReviewers().forEach(reviewer -> validateUser(reviewer, "Reviewer"));
//
//            validateSOP(sopDTO);
//            SOPInitiation convertedSOP = convertDTOToEntity(sopDTO);
//            SOPInitiation savedSOP = sopRepository.save(convertedSOP);
//
//            notificationService.notifyAuthor(savedSOP);
//            return convertEntityToDTO(savedSOP);
//        } catch (InvalidSOPException e) {
//            logger.error("Failed to initiate SOP: {}", e.getMessage());
//            throw e;
//        } catch (FeignException e) {
//            logger.error("Failed to validate user through authentication service: {}", e.getMessage());
//            throw new RuntimeException("Failed to validate user", e);
//        } catch (Exception e) {
//            logger.error("Unexpected error while initiating SOP", e);
//            throw new RuntimeException("Failed to initiate SOP", e);
//        }
//    }
//
//    private void validateUser(String userId, String role) {
//        try {
//            // Calling the AuthenticationServiceClient to validate the user
//            ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                logger.info("{} with ID {} is valid", role, userId);
//            } else {
//                throw new InvalidSOPException(role + " with ID " + userId + " is not valid");
//            }
//        } catch (FeignException.NotFound e) {
//            throw new InvalidSOPException(role + " with ID " + userId + " does not exist");
//        } catch (FeignException e) {
//            logger.error("Error validating {} with ID {}: {}", role, userId, e.getMessage());
//            throw new RuntimeException("Error validating " + role, e);
//        }
//    }
//
//    private void validateSOP(SOPInitiationDTO sop) {
//        if (sop.getTitle() == null || sop.getTitle().isEmpty()) {
//            throw new InvalidSOPException("SOP title cannot be empty");
//        }
//        if (sop.getVisibility() == null) {
//            throw new InvalidSOPException("SOP visibility must be specified");
//        }
//        validateApprovalPipeline(sop.getApprovalPipeline(), sop.getVisibility());
//    }
//
//    private void validateApprovalPipeline(ApprovalPipeline pipeline, Visibility visibility) {
//        if (pipeline == null || pipeline.getAuthor() == null || pipeline.getApprover() == null ||
//                pipeline.getReviewers() == null || pipeline.getReviewers().isEmpty()) {
//            throw new InvalidSOPException("Invalid approval pipeline. Author, approver, and reviewers must be defined.");
//        }
//
//        String hod = pipeline.getApprover();
//
//        if (visibility == Visibility.DEPARTMENT) {
//            if (!isSameDepartment(hod, pipeline.getAuthor()) ||
//                    !pipeline.getReviewers().stream().allMatch(reviewer -> isSameDepartment(hod, reviewer))) {
//                throw new InvalidSOPException("For department visibility, all staff must be from the same department.");
//            }
//        }
//
//        if (!isValidApprover(pipeline.getApprover(), hod)) {
//            throw new InvalidSOPException("Approver must be the HoD or from the same department as the HoD.");
//        }
//
//        logger.info("Approval pipeline validated successfully");
//    }
//    private boolean isSameDepartment(String user1, String user2) {
//        return user1.equals(user2); // Replace with actual department check logic
//    }
//
//    private boolean isValidApprover(String approver, String hod) {
//        return approver.equals(hod) || isSameDepartment(approver, hod);
//    }
//
//        @Transactional(propagation = Propagation.REQUIRED)
//    public void deleteSOP(String id) {
//        try {
//            logger.info("Deleting SOP with id: {}", id);
//            sopRepository.deleteById(id);
//        } catch (Exception e) {
//            logger.error("Failed to delete SOP with id: {}", id, e);
//            throw new RuntimeException("Failed to delete SOP", e);
//        }
//    }
//
//    public List<SOPInitiationDTO> getAllSOPs() {
//        logger.info("Fetching all SOPs");
//        try {
//            List<SOPInitiation> sops = sopRepository.findAll();
//            logger.info("Retrieved {} SOPs", sops.size());
//            return sops.stream()
//                    .map(this::convertEntityToDTO)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            logger.error("Error fetching all SOPs: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch all SOPs", e);
//        }
//    }
//
//    public SOPInitiationDTO getSOPById(String id) throws SOPNotFoundException {
//        logger.info("Fetching SOP with ID: {}", id);
//        try {
//            SOPInitiation sop = sopRepository.findById(id)
//                    .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
//            logger.info("Retrieved SOP with ID: {}", id);
//            return convertEntityToDTO(sop);
//        } catch (SOPNotFoundException e) {
//            logger.error("SOP not found with ID: {}", id);
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error fetching SOP with ID {}: {}", id, e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch SOP", e);
//        }
//    }
//
//    public List<SOPInitiationDTO> getSOPsByVisibility(Visibility visibility) {
//        logger.info("Fetching SOPs with visibility: {}", visibility);
//        try {
//            List<SOPInitiation> sops = sopRepository.findByVisibility(visibility);
//            logger.info("Retrieved {} SOPs with visibility {}", sops.size(), visibility);
//            return sops.stream()
//                    .map(this::convertEntityToDTO)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            logger.error("Error fetching SOPs with visibility {}: {}", visibility, e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch SOPs by visibility", e);
//        }
//    }
//
//    public List<SOPInitiationDTO> getSOPsByAuthor(String authorId) {
//        logger.info("Fetching SOPs by author ID: {}", authorId);
//        try {
//            List<SOPInitiation> sops = sopRepository.findByApprovalPipeline_Author_Id(authorId);
//            logger.info("Retrieved {} SOPs for author {}", sops.size(), authorId);
//            return sops.stream()
//                    .map(this::convertEntityToDTO)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            logger.error("Error fetching SOPs for author {}: {}", authorId, e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch SOPs by author", e);
//        }
//    }
//
//
//    // Conversion methods remain the same
//
//    private SOPInitiation convertDTOToEntity(SOPInitiationDTO dto) {
//        SOPInitiation entity = new SOPInitiation();
//        entity.setSopId(dto.getSopId());
//        entity.setTitle(dto.getTitle());
//        entity.setVisibility(dto.getVisibility());
//
//        ApprovalPipeline approvalPipeline = new ApprovalPipeline();
//        approvalPipeline.setAuthor(dto.getApprovalPipeline().getAuthor());
//        approvalPipeline.setApprover(dto.getApprovalPipeline().getApprover());
//        approvalPipeline.setReviewers(dto.getApprovalPipeline().getReviewers());
//
//        entity.setApprovalPipeline(approvalPipeline);
//        return entity;
//    }
//
//    private SOPInitiationDTO convertEntityToDTO(SOPInitiation entity) {
//        return new SOPInitiationDTO(
//                entity.getSopId(),
//                entity.getTitle(),
//                entity.getVisibility(),
//                new ApprovalPipelineDto(
//                        entity.getApprovalPipeline().getAuthor(),
//                        entity.getApprovalPipeline().getApprover(),
//                        entity.getApprovalPipeline().getReviewers()
//                )
//        );
//    }
//}


package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
//import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.dto.SOPInitiationDTO;
import com.team.sop_management_service.error.InvalidSOPException;
import com.team.sop_management_service.error.SOPNotFoundException;
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
 //   private final NotificationService notificationService;
    private final AuthenticationServiceClient authenticationServiceClient;

    @Autowired
    public SOPInitiationService(SOPInitiationRepository sopRepository,  AuthenticationServiceClient authenticationServiceClient) {
        this.sopRepository = sopRepository;
      //  this.notificationService = notificationService;
        this.authenticationServiceClient = authenticationServiceClient;
    }

    // Method to initiate a Standard Operating Procedure (SOP)
    @Transactional(propagation = Propagation.REQUIRED)
    public SOPInitiationDTO initiateSOP(SOPInitiationDTO sopDTO) {
        try {
            logger.info("Initiating SOP: {}", sopDTO.getTitle());

            // Validate users involved in the SOP process
            validateUser(sopDTO.getApprovalPipeline().getAuthor(), "Author");
            validateUser(sopDTO.getApprovalPipeline().getApprover(), "Approver");
            sopDTO.getApprovalPipeline().getReviewers().forEach(reviewer -> validateUser(reviewer, "Reviewer"));

            // Validate the SOP details
            validateSOP(sopDTO);
            SOPInitiation convertedSOP = convertDTOToEntity(sopDTO); // Convert DTO to entity
            SOPInitiation savedSOP = sopRepository.save(convertedSOP); // Save the SOP to the repository

            // Notify the author about the new task
           // notificationService.notifyAuthor(savedSOP);
            return convertEntityToDTO(savedSOP); // Convert entity back to DTO for response
        } catch (InvalidSOPException e) {
            logger.error("Failed to initiate SOP: {}", e.getMessage());
            throw e; // Propagate the exception if SOP validation fails
        } catch (FeignException e) {
            logger.error("Failed to validate user through authentication service: {}", e.getMessage());
            throw new RuntimeException("Failed to validate user", e); // Handle external service failures
        } catch (Exception e) {
            logger.error("Unexpected error while initiating SOP", e);
            throw new RuntimeException("Failed to initiate SOP", e); // Handle unexpected errors
        }
    }

    // Validate a user based on their ID and role
    private void validateUser(int userId, String role) {
        try {
            // Calling the AuthenticationServiceClient to validate the user
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

    // Validate the details of the SOP initiation
    private void validateSOP(SOPInitiationDTO sop) {
        if (sop.getTitle() == null || sop.getTitle().isEmpty()) {
            throw new InvalidSOPException("SOP title cannot be empty");
        }
        if (sop.getVisibility() == null) {
            throw new InvalidSOPException("SOP visibility must be specified");
        }
        validateApprovalPipeline(sop.getApprovalPipeline(), sop.getVisibility()); // Validate the approval pipeline
    }

    // Validate the approval pipeline
    private void validateApprovalPipeline(ApprovalPipeline pipeline, Visibility visibility) {
        if (pipeline == null || pipeline.getAuthor() == 0 || pipeline.getApprover() == 0 ||
                pipeline.getReviewers() == null || pipeline.getReviewers().isEmpty()) {
            throw new InvalidSOPException("Invalid approval pipeline. Author, approver, and reviewers must be defined.");
        }

        int hod = pipeline.getApprover(); // HoD should be the approver

        // Check for department visibility
        if (visibility == Visibility.DEPARTMENT) {
            if (!isSameDepartment(hod, pipeline.getAuthor()) ||
                    !pipeline.getReviewers().stream().allMatch(reviewer -> isSameDepartment(hod, reviewer))) {
                throw new InvalidSOPException("For department visibility, all staff must be from the same department.");
            }
        }

        // Check if the approver is valid
        if (!isValidApprover(pipeline.getApprover(), hod)) {
            throw new InvalidSOPException("Approver must be the HoD or from the same department as the HoD.");
        }

        logger.info("Approval pipeline validated successfully");
    }

    // Check if two users are from the same department using the authentication service
    private boolean isSameDepartment(int user1Id, int user2Id) {
        // Fetch user details for both users from the authentication service
        ResponseEntity<UserDto> user1Response = authenticationServiceClient.getUserById(user1Id);
        ResponseEntity<UserDto> user2Response = authenticationServiceClient.getUserById(user2Id);

        // Check if both responses are successful and retrieve user details
        if (user1Response.getStatusCode().is2xxSuccessful() && user2Response.getStatusCode().is2xxSuccessful()) {
            UserDto user1 = user1Response.getBody();
            UserDto user2 = user2Response.getBody();

            if (user1 != null && user2 != null) {
                String department1 = String.valueOf(user1.getDepartment());
                String department2 = String.valueOf(user2.getDepartment());
                return department1 != null && department1.equals(department2);
            }
        }

        // Return false if either user is not found or the responses are not successful
        return false;
    }

    // Check if the approver is valid
    private boolean isValidApprover(int approver, int hod) {
        return approver==(hod) || isSameDepartment(approver, hod);
    }

    // Method to delete an SOP by its ID
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

    // Method to get all SOPs
    public List<SOPInitiationDTO> getAllSOPs() {
        logger.info("Fetching all SOPs");
        try {
            List<SOPInitiation> sops = sopRepository.findAll();
            logger.info("Retrieved {} SOPs", sops.size());
            return sops.stream()
                    .map(this::convertEntityToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all SOPs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch all SOPs", e);
        }
    }

    // Method to fetch an SOP by its ID
    public SOPInitiation getSOPById(String id) throws SOPNotFoundException {
        logger.info("Fetching SOP with ID: {}", id);
        try {
            SOPInitiation sop = sopRepository.findById(id)
                    .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
            logger.info("Retrieved SOP with ID: {}", id);
            return sop;
        } catch (SOPNotFoundException e) {
            logger.error("SOP not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching SOP with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SOP", e);
        }
    }

    // Method to get SOPs by their visibility
    public List<SOPInitiationDTO> getSOPsByVisibility(Visibility visibility) {
        logger.info("Fetching SOPs with visibility: {}", visibility);
        try {
            List<SOPInitiation> sops = sopRepository.findByVisibility(visibility);
            logger.info("Retrieved {} SOPs with visibility {}", sops.size(), visibility);
            return sops.stream()
                    .map(this::convertEntityToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching SOPs with visibility {}: {}", visibility, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SOPs by visibility", e);
        }
    }

    // Method to get SOPs by author ID
    public List<SOPInitiationDTO> getSOPsByAuthor(int authorId) {
        logger.info("Fetching SOPs by author ID: {}", authorId);
        try {
            List<SOPInitiation> sops = sopRepository.findByApprovalPipeline_Author_Id(authorId);
            logger.info("Retrieved {} SOPs for author {}", sops.size(), authorId);
            return sops.stream()
                    .map(this::convertEntityToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching SOPs by author ID {}: {}", authorId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch SOPs by author", e);
        }
    }

    // Convert SOPInitiationDTO to SOPInitiation entity
    private SOPInitiation convertDTOToEntity(SOPInitiationDTO dto) {
        SOPInitiation entity = new SOPInitiation();
        entity.setSopId(dto.getSopId());
        entity.setTitle(dto.getTitle());
        entity.setVisibility(dto.getVisibility());

        ApprovalPipeline approvalPipeline = new ApprovalPipeline();
        approvalPipeline.setAuthor(dto.getApprovalPipeline().getAuthor());
        approvalPipeline.setApprover(dto.getApprovalPipeline().getApprover());
        approvalPipeline.setReviewers(dto.getApprovalPipeline().getReviewers());

        entity.setApprovalPipeline(approvalPipeline);
        return entity;
    }

    private SOPInitiationDTO convertEntityToDTO(SOPInitiation entity) {
        return new SOPInitiationDTO(
                entity.getSopId(),
                entity.getTitle(),
                entity.getVisibility()
//                new ApprovalPipelineDto(
//                        entity.getApprovalPipeline().getAuthor(),
//                        entity.getApprovalPipeline().getApprover(),
//                        entity.getApprovalPipeline().getReviewers()
//                )
        );
    }
}
