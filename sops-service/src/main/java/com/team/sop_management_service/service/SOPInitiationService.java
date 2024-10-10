package com.team.sop_management_service.service;

import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.error.InvalidSOPException;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.models.ApprovalPipeline;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import com.team.sop_management_service.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SOPInitiationService {
    private static final Logger logger = LoggerFactory.getLogger(SOPInitiationService.class);

    private final SOPInitiationRepository sopRepository;
    private final NotificationService notificationService;

    @Autowired
    public SOPInitiationService(SOPInitiationRepository sopRepository, NotificationService notificationService) {
        this.sopRepository = sopRepository;
        this.notificationService = notificationService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SOPInitiation initiateSOP(SOPInitiation sop) {
        try {
            logger.info("Initiating SOP: {}", sop.getTitle());
            validateSOP(sop);
            SOPInitiation savedSOP = sopRepository.save(sop);

            notificationService.notifyAuthor(savedSOP);
             return savedSOP;
        } catch (InvalidSOPException e) {
            logger.error("Failed to initiate SOP: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while initiating SOP", e);
            throw new RuntimeException("Failed to initiate SOP", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SOPInitiation saveSOP(SOPInitiation sop) {
        try {
            logger.info("Saving SOP: {}", sop.getTitle());
            return sopRepository.save(sop);
        } catch (Exception e) {
            logger.error("Failed to save SOP", e);
            throw new RuntimeException("Failed to save SOP", e);
        }
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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SOPInitiation> getAllSOPs() {
        try {
            logger.info("Retrieving all SOPs");
            return sopRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve all SOPs", e);
            throw new RuntimeException("Failed to retrieve SOPs", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SOPInitiation getSOPById(String id) {
        try {
            logger.info("Retrieving SOP with id: {}", id);
            return sopRepository.findById(id)
                    .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
        } catch (SOPNotFoundException e) {
            logger.error("SOP not found with id: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve SOP with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve SOP", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SOPInitiation> getSOPsByVisibility(Visibility visibility) {
        try {
            logger.info("Retrieving SOPs with visibility: {}", visibility);
            return sopRepository.findByVisibility(visibility);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by visibility: {}", visibility, e);
            throw new RuntimeException("Failed to retrieve SOPs by visibility", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SOPInitiation> getSOPsByAuthor(String authorId) {
        try {
            logger.info("Retrieving SOPs by author id: {}", authorId);
            return sopRepository.findByApprovalPipeline_Author_Id(authorId);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by author id: {}", authorId, e);
            throw new RuntimeException("Failed to retrieve SOPs by author", e);
        }
    }

    private void validateSOP(SOPInitiation sop) {
        if (sop.getTitle() == null || sop.getTitle().isEmpty()) {
            throw new InvalidSOPException("SOP title cannot be empty");
        }
        if (sop.getVisibility() == null) {
            throw new InvalidSOPException("SOP visibility must be specified");
        }
        validateApprovalPipeline(sop.getApprovalPipeline(), sop.getVisibility());
    }

    /**
        * Validates the approval pipeline for department-level visibility and ensures the approver is valid.
          */

    private void validateApprovalPipeline(ApprovalPipeline pipeline, Visibility visibility) {
        // Ensure that the approval pipeline has all necessary roles
        if (pipeline == null || pipeline.getAuthor() == null || pipeline.getApprover() == null ||
                pipeline.getReviewers() == null || pipeline.getReviewers().isEmpty()) {
            throw new InvalidSOPException("Invalid approval pipeline. Author, approver, and reviewers must be defined.");
        }
//        // Check if the author, approver, and reviewers exist in the system
//        checkUserExists(pipeline.getAuthor());
//        checkUserExists(pipeline.getApprover());
//        pipeline.getReviewers().forEach(this::checkUserExists);

        User hod = pipeline.getApprover(); // Assume the HoD is the approver or a staff member

        // Visibility is Department: All members must be from the same department as the HoD
        if (visibility == Visibility.DEPARTMENT) {
            if (!isSameDepartment(hod, pipeline.getAuthor()) ||
                    !pipeline.getReviewers().stream().allMatch(reviewer -> isSameDepartment(hod, reviewer))) {
                throw new InvalidSOPException("For department visibility, all staff must be from the same department.");
            }
        }

        // Validate that the approver is either the HoD or a valid staff member
        if (!isValidApprover(pipeline.getApprover(), hod)) {
            throw new InvalidSOPException("Approver must be the HoD or from the same department as the HoD.");
        }

        logger.info("Approval pipeline validated successfully");

        // Notify the Author after successful validation
       // notifyAuthor(pipeline);
    }
//    /**
//     * Checks if the user exists in the system (e.g., by querying the database).
//     * Throws an exception if the user doesn't exist.
//     * @param user The user to check.
//     */
//    private void checkUserExists(User user) {
//        // Assuming userService is a service that checks the user’s existence
//        if (!userService.existsById(user.getId())) {
//            throw new InvalidSOPException("User with ID " + user.getId() + " does not exist.");
//        }
//    }

    // Checks if both users belong to the same department
    private boolean isSameDepartment(User user1, User user2) {
        return user1.getDepartment().equals(user2.getDepartment());
    }

    // Validates whether the approver is the HoD or belongs to the same department
    private boolean isValidApprover(User approver, User hod) {
        return approver.equals(hod) || isSameDepartment(approver, hod);
    }

//    // Notifies the Author about the SOP assignment
//    private void notifyAuthor(ApprovalPipeline pipeline) {
//        try {
//            SOP sop = pipeline.getSop(); // Assuming pipeline contains a reference to the SOP
//            notificationService.sendAuthorNotification(sop);
//        } catch (Exception e) {
//            logger.error("Failed to send notification to author", e);
//            // We do not throw an exception to avoid rolling back the SOP creation
//        }
//    }
}