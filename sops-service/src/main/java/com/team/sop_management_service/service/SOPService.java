package com.team.sop_management_service.service;

import com.team.sop_management_service.error.InvalidSOPException;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.models.ApprovalPipeline;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.repository.SOPRepository;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.enums.SOPStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class SOPService {
    private static final Logger logger = LoggerFactory.getLogger(SOPService.class);

    private final SOPRepository sopRepository;
   // private final NotificationService notificationService;

//    @Autowired
//    public SOPService(SOPRepository sopRepository, NotificationService notificationService) {
//        this.sopRepository = sopRepository;
//        this.notificationService = notificationService;
//    }


    @Autowired
    public SOPService(SOPRepository sopRepository) {
        this.sopRepository = sopRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SOP initiateSOP(SOP sop) {
        try {
            logger.info("Initiating SOP: {}", sop.getTitle());
            validateSOP(sop);
            sop.setStatus(SOPStatus.DRAFT);
            SOP savedSOP = sopRepository.save(sop);
           // notifyAuthor(savedSOP);
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
    public SOP saveSOP(SOP sop) {
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
    public List<SOP> getAllSOPs() {
        try {
            logger.info("Retrieving all SOPs");
            return sopRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to retrieve all SOPs", e);
            throw new RuntimeException("Failed to retrieve SOPs", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SOP getSOPById(String id) {
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
    public List<SOP> getSOPsByVisibility(Visibility visibility) {
        try {
            logger.info("Retrieving SOPs with visibility: {}", visibility);
            return sopRepository.findByVisibility(visibility);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by visibility: {}", visibility, e);
            throw new RuntimeException("Failed to retrieve SOPs by visibility", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SOP> getSOPsByStatus(SOPStatus status) {
        try {
            logger.info("Retrieving SOPs with status: {}", status);
            return sopRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by status: {}", status, e);
            throw new RuntimeException("Failed to retrieve SOPs by status", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<SOP> getSOPsByAuthor(String authorId) {
        try {
            logger.info("Retrieving SOPs by author id: {}", authorId);
            return sopRepository.findByApprovalPipeline_Author_Id(authorId);
        } catch (Exception e) {
            logger.error("Failed to retrieve SOPs by author id: {}", authorId, e);
            throw new RuntimeException("Failed to retrieve SOPs by author", e);
        }
    }

    private void validateSOP(SOP sop) {
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