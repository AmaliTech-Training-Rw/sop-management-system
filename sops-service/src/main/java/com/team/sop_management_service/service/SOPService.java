//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.error.InvalidPipelineException;
//import com.team.sop_management_service.error.SOPInitiationException;
//import com.team.sop_management_service.error.SOPRetrievalException;
//import com.team.sop_management_service.models.ApprovalPipeline;
//import com.team.sop_management_service.models.Department;
//import com.team.sop_management_service.models.SOP;
//import com.team.sop_management_service.models.User;
//import com.team.sop_management_service.repository.SOPRepository;
//import com.team.sop_management_service.enums.Visibility;
//import com.team.sop_management_service.enums.SOPStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
//@Service
//public class SOPService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPService.class);
//
//    private final SOPRepository sopRepository;
//   // private final UserRepository userRepository;
//   // private final DepartmentRepository departmentRepository;
//   // private final NotificationFactory notificationFactory;
//
//    // Constructor injection of required dependencies
////    @Autowired
////    public SOPService(SOPRepository sopRepository, //UserRepository userRepository,
////                      DepartmentRepository departmentRepository, NotificationFactory notificationFactory) {
////        this.sopRepository = sopRepository;
////        this.userRepository = userRepository;
////        this.departmentRepository = departmentRepository;
////        this.notificationFactory = notificationFactory;
////    }
//
//
//    public SOPService(SOPRepository sopRepository) {
//        this.sopRepository = sopRepository;
//    }
//
//    /**
//     * Initiates a new SOP with the provided title, visibility, approval pipeline, and HoD.
//     * Validates the pipeline before saving the SOP and sends notifications.
//     */
//    @Transactional
//    public SOP initiateSOP(String title, Visibility visibility, ApprovalPipeline approvalPipeline, User hod) {
//        try {
//            logger.info("Initiating SOP with title: {}, visibility: {}, and HoD: {}", title, visibility, hod.getName());
//
//            // Validate the approval pipeline based on HoD and visibility
//            validatePipeline(approvalPipeline, hod, visibility);
//
//            // Create and save the SOP
//            SOP sop = new SOP();
//            sop.setTitle(title);
//            sop.setVisibility(visibility);
//            sop.setApprovalPipeline(approvalPipeline);
//            sop.setStatus(SOPStatus.DRAFT);
//
//            SOP savedSOP = sopRepository.save(sop);
//
//            logger.info("SOP initiated successfully: {}", savedSOP);
//
//            // Send notification to the author
//            sendNotification(savedSOP);
//
//            return savedSOP;
//        } catch (InvalidPipelineException e) {
//            logger.error("Invalid pipeline: {}", e.getMessage(), e);
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error initiating SOP: {}", e.getMessage(), e);
//            throw new SOPInitiationException("Failed to initiate SOP", e);
//        }
//    }
//
//    /**
//     * Validates the approval pipeline for department-level visibility and ensures the approver is valid.
//     */
//    private void validatePipeline(ApprovalPipeline pipeline, User hod, Visibility visibility) {
//        try {
//            if (visibility == Visibility.DEPARTMENT) {
//                // Ensure all staff in the pipeline belong to the same department for department-level visibility
//                if (!isSameDepartment(hod, pipeline)) {
//                    logger.warn("Pipeline validation failed: staff not from the same department");
//                    throw new InvalidPipelineException("All staff must be from the same department for department-level visibility.");
//                }
//            }
//
//            // Ensure the approver is valid (must be HoD or a staff member)
//            if (!isValidApprover(pipeline.getApprover(), hod)) {
//                logger.warn("Pipeline validation failed: approver is not valid");
//                throw new InvalidPipelineException("Approver must be a staff member or the HoD.");
//            }
//
//            logger.info("Pipeline validated successfully");
//        } catch (Exception e) {
//            logger.error("Error validating pipeline: {}", e.getMessage(), e);
//            throw new InvalidPipelineException("Failed to validate pipeline", e);
//        }
//    }
//
//    /**
//     * Checks if all users in the approval pipeline belong to the same department as the HoD.
//     */
//    private boolean isSameDepartment(User hod, ApprovalPipeline pipeline) {
//        Department hodDepartment = hod.getDepartment();
//        return pipeline.getAuthor().getDepartment().equals(hodDepartment) &&
//                pipeline.getApprover().getDepartment().equals(hodDepartment) &&
//                pipeline.getReviewers().stream().allMatch(reviewer -> reviewer.getDepartment().equals(hodDepartment));
//    }
//
//    /**
//     * Validates whether the approver is the HoD or from the same department as the HoD.
//     */
//    private boolean isValidApprover(User approver, User hod) {
//        return approver.equals(hod) || approver.getDepartment().equals(hod.getDepartment());
//    }
//
//    /**
//     * Sends a notification to the SOP author once the SOP is successfully initiated.
//     */
//    private void sendNotification(SOP sop) {
//        try {
//            Notification notification = notificationFactory.createNotification(
//                    sop.getApprovalPipeline().getAuthor().getEmail(),
//                    "New SOP Task",
//                    "You have been assigned as the Author for the SOP: " + sop.getTitle()
//            );
//            notification.send();
//            logger.info("Notification sent successfully to {}", sop.getApprovalPipeline().getAuthor().getEmail());
//        } catch (Exception e) {
//            logger.error("Failed to send notification: {}", e.getMessage(), e);
//            // No rethrow, as failure to send a notification shouldn't prevent SOP initiation
//        }
//    }
//
//    /**
//     * Retrieves all SOPs from the database.
//     */
//    public List<SOP> getAllSOPs() {
//        try {
//            logger.info("Retrieving all SOPs");
//            return sopRepository.findAll();
//        } catch (Exception e) {
//            logger.error("Error retrieving SOPs: {}", e.getMessage(), e);
//            throw new SOPRetrievalException("Failed to retrieve SOPs", e);
//        }
//    }
//}
//


package com.team.sop_management_service.service;

import com.team.sop_management_service.error.InvalidPipelineException;
import com.team.sop_management_service.error.SOPInitiationException;
import com.team.sop_management_service.error.SOPRetrievalException;
import com.team.sop_management_service.models.ApprovalPipeline;
import com.team.sop_management_service.models.Department;
import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.repository.SOPRepository;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.enums.SOPStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SOPService {
    private static final Logger logger = LoggerFactory.getLogger(SOPService.class);

    @Autowired
    private final SOPRepository sopRepository;

    public SOPService(SOPRepository sopRepository) {
        this.sopRepository = sopRepository;
    }

//    @Autowired
//    public SOPService(@Qualifier("sopRepository")SOPRepository sopRepository) {
//        this.sopRepository = sopRepository;
//    }

    @Transactional
//    public SOP initiateSOP(String title, Visibility visibility, ApprovalPipeline approvalPipeline, String hodDepartmentId) {
       public SOP initiateSOP(SOP sop, String hodDepartmentId) {

        try {
            logger.info("Initiating SOP with title: {}, visibility: {}", sop.getTitle(), sop.getVisibility());

            // Validate the approval pipeline based on HoD department ID and visibility
        //    validatePipeline(sop.getApprovalPipeline(), hodDepartmentId, sop.getVisibility());

            //SOP sop = new SOP();
//            sop.setTitle(title);
//            sop.setVisibility(visibility);
//            sop.setApprovalPipeline(approvalPipeline);
//            sop.setStatus(SOPStatus.DRAFT);

            SOP savedSOP = sopRepository.save(sop);
            logger.info("SOP initiated successfully: {}", savedSOP);

            sendNotification(savedSOP);
            return savedSOP;
        } catch (InvalidPipelineException e) {
            logger.error("Invalid pipeline: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error initiating SOP: {}", e.getMessage(), e);
            throw new SOPInitiationException("Failed to initiate SOP", e);
        }
    }

    private void validatePipeline(ApprovalPipeline pipeline, String hodDepartmentId, Visibility visibility) {
        try {
            if (visibility == Visibility.DEPARTMENT) {
                // Ensure all staff in the pipeline belong to the same department for department-level visibility
                if (!isSameDepartment(String.valueOf(Integer.parseInt(hodDepartmentId)), pipeline)) {
                    logger.warn("Pipeline validation failed: staff not from the same department");
                    throw new InvalidPipelineException("All staff must be from the same department for department-level visibility.");
                }
            }

            // Ensure the approver is valid (must be HoD or a staff member)
            if (!isValidApprover(pipeline.getApprover(), hodDepartmentId)) {
                logger.warn("Pipeline validation failed: approver is not valid");
                throw new InvalidPipelineException("Approver must be a staff member or the HoD.");
            }

            logger.info("Pipeline validated successfully");
        } catch (Exception e) {
            logger.error("Error validating pipeline: {}", e.getMessage(), e);
            throw new InvalidPipelineException("Failed to validate pipeline");
        }
    }

    private boolean isSameDepartment(String hodDepartmentId, ApprovalPipeline pipeline) {
        // This is a placeholder for the actual department comparison logic
        // You might want to check against a list of user departments to ensure they match
        return pipeline.getAuthor().equals(hodDepartmentId) &&
                pipeline.getApprover().equals(hodDepartmentId) &&
                pipeline.getReviewers().stream().allMatch(reviewerId -> reviewerId.equals(hodDepartmentId));
    }

    private boolean isValidApprover(String approverId, String hodDepartmentId) {
        // Implement logic to check if the approverId matches the HoD or is from the same department
        // For simplicity, just a comparison here
        return approverId.equals(hodDepartmentId) || approverId.equals(hodDepartmentId); // This might need more logic
    }

    private void sendNotification(SOP sop) {
        // Implement the notification sending logic here
    }

    public List<SOP> getAllSOPs() {
        try {
            logger.info("Retrieving all SOPs");
            return sopRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving SOPs: {}", e.getMessage(), e);
            throw new SOPRetrievalException("Failed to retrieve SOPs", e);
        }
    }
}
