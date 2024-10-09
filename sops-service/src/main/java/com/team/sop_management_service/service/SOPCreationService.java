//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class SOPCreationService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);
//    private final SOPCreationRepository sopCreationRepository;
//    private final FileStorageService fileStorageService;
//
//    /**
//     * Creates a new SOP with associated files.
//     *
//     * @param sopCreation The SOP to be created
//     * @param files The list of files to be associated with the SOP
//     * @return The created SOP
//     * @throws RuntimeException if there's an error storing the files
//     */
//    public SOPCreation createSOP(SOPCreation sopCreation, List<MultipartFile> files) throws RuntimeException {
//        logger.info("Creating new SOP: {}", sopCreation.getTitle());
//        sopCreation.setCreated_at(LocalDateTime.now());
//        sopCreation.setApproved(false);
//        sopCreation.setVersion(1);
//
//        if (files != null && !files.isEmpty()) {
//            List<String> fileNames = new ArrayList<>();
//            for (MultipartFile file : files) {
//                try {
//                    String fileName = fileStorageService.storeFile(file);
//                    fileNames.add(fileName);
//                } catch (IOException e) {
//                    logger.error("Failed to store file: {}", file.getOriginalFilename(), e);
//                    throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
//                }
//            }
//            sopCreation.setFiles(fileNames);
//        }
//
//        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
//        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
//        return savedSOP;
//    }
//
//    /**
//     * Retrieves an SOP by its ID.
//     *
//     * @param id The ID of the SOP
//     * @return The SOP if found
//     * @throws SOPNotFoundException if the SOP is not found
//     */
//    public SOPCreation getSOPById(String id) throws SOPNotFoundException {
//        logger.debug("Fetching SOP with ID: {}", id);
//        return sopCreationRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.warn("SOP not found with ID: {}", id);
//                    return new SOPNotFoundException("SOP not found with id: " + id);
//                });
//    }
//
//    /**
//     * Retrieves all SOPs.
//     *
//     * @return List of all SOPs
//     */
//    public List<SOPCreation> getAllSOPs() {
//        logger.debug("Fetching all SOPs");
//        return sopCreationRepository.findAll();
//    }
//
//    /**
//     * Updates an existing SOP.
//     *
//     * @param id The ID of the SOP to update
//     * @param sopCreation The updated SOP data
//     * @return The updated SOP
//     * @throws SOPNotFoundException if the SOP is not found
//     */
//    public SOPCreation updateSOP(String id, SOPCreation sopCreation) throws SOPNotFoundException {
//        logger.info("Updating SOP with ID: {}", id);
//        SOPCreation existingSOP = getSOPById(id);
//        existingSOP.setTitle(sopCreation.getTitle());
//        existingSOP.setDescription(sopCreation.getDescription());
//        existingSOP.setContent(sopCreation.getContent());
//        existingSOP.setVersion(existingSOP.getVersion() + 1);
//        existingSOP.setUpdated_at(LocalDateTime.now());
//
//        SOPCreation updatedSOP = sopCreationRepository.save(existingSOP);
//        logger.info("SOP updated successfully with ID: {}", updatedSOP.getId());
//        return updatedSOP;
//    }
//
//    /**
//     * Deletes an SOP.
//     *
//     * @param id The ID of the SOP to delete
//     * @throws SOPNotFoundException if the SOP is not found
//     */
//    public void deleteSOP(String id) throws SOPNotFoundException {
//        logger.info("Deleting SOP with ID: {}", id);
//        if (!sopCreationRepository.existsById(id)) {
//            logger.warn("Attempted to delete non-existent SOP with ID: {}", id);
//            throw new SOPNotFoundException("SOP not found with id: " + id);
//        }
//        sopCreationRepository.deleteById(id);
//        logger.info("SOP deleted successfully with ID: {}", id);
//    }
//
//    /**
//     * Retrieves an SOP by its reference ID.
//     *
//     * @param sopReferenceId The reference ID of the SOP
//     * @return The SOP if found
//     * @throws SOPNotFoundException if the SOP is not found
//     */
//    public SOPCreation getSOPByReferenceId(String sopReferenceId) throws SOPNotFoundException {
//        logger.debug("Fetching SOP with reference ID: {}", sopReferenceId);
//        return sopCreationRepository.findBySopReferenceId(sopReferenceId)
//                .orElseThrow(() -> {
//                    logger.warn("SOP not found with reference ID: {}", sopReferenceId);
//                    return new SOPNotFoundException("SOP not found with reference id: " + sopReferenceId);
//                });
//    }
//}


package com.team.sop_management_service.service;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SOPCreationService {
    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);
    private final SOPCreationRepository sopCreationRepository;
    private final SOPInitiationRepository sopInitiationRepository;
    private final FileStorageService fileStorageService;
   // private final EmailNotificationService emailNotificationService;

    public SOPCreation createSOP(SOPCreation sopCreation, List<MultipartFile> files, String createdBy, String sopId) throws SOPNotFoundException {
        logger.info("Creating new SOP: {}", sopCreation.getTitle());
        sopCreation.setCreatedAt(LocalDateTime.now());
        sopCreation.setApproved(false);
        sopCreation.setVersion(1);  // Set initial version
        sopCreation.setStatus(SOPStatus.DRAFT);  // Initially set to draft

        // Attach the SOPInitiation reference
        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP Initiation not found with id: " + sopId));
        sopCreation.setSopInitiation(sopInitiation);

        // Handle file uploads
        if (files != null && !files.isEmpty()) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String fileName = fileStorageService.storeFile(file);
                    fileNames.add(fileName);
                } catch (IOException e) {
                    logger.error("Failed to store file: {}", file.getOriginalFilename(), e);
                    throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
                }
            }
            sopCreation.setFiles(fileNames);  // Set file names/paths
        }

        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
        return savedSOP;
    }

    public SOPCreation submitSOPForReview(String id, String updatedBy) throws SOPNotFoundException {
        SOPCreation sopCreation = getSOPById(id);
        sopCreation.setStatus(SOPStatus.SUBMITTED);  // Change status to submitted
        sopCreation.setUpdatedAt(LocalDateTime.now());

        sopCreationRepository.save(sopCreation);

        // Notify reviewers
      //  emailNotificationService.notifyReviewers(sopCreation);

        return sopCreation;
    }

    public SOPCreation getSOPById(String id) throws SOPNotFoundException {
        return sopCreationRepository.findById(id)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
    }

    @Cacheable(value = "sop_cache", key = "#id")
    public SOPCreation getCachedSOPById(String id) throws SOPNotFoundException {
        return getSOPById(id);
    }

    public List<SOPCreation> getAllSOPs() {
        return sopCreationRepository.findAll();
    }

    public SOPCreation updateSOP(String id, SOPCreation updatedSOP) throws SOPNotFoundException {
        SOPCreation existingSOP = getSOPById(id);
        existingSOP.setTitle(updatedSOP.getTitle());
        existingSOP.setDescription(updatedSOP.getDescription());
        existingSOP.setContent(updatedSOP.getContent());
        existingSOP.setVersion(existingSOP.getVersion() + 1);  // Increment version on update
        existingSOP.setUpdatedAt(LocalDateTime.now());

        return sopCreationRepository.save(existingSOP);
    }

    public void deleteSOP(String id) throws SOPNotFoundException {
        if (!sopCreationRepository.existsById(id)) {
            throw new SOPNotFoundException("SOP not found with id: " + id);
        }
        sopCreationRepository.deleteById(id);
    }
}
