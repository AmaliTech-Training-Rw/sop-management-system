//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.enums.SOPStatus;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.models.SOPInitiation;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SOPCreationService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);
//    private final SOPCreationRepository sopCreationRepository;
//    private final SOPInitiationRepository sopInitiationRepository;
//    private final FileStorageService fileStorageService;
//   // private final EmailNotificationService emailNotificationService;
//
//    public SOPCreation createSOP(SOPCreation sopCreation) throws SOPNotFoundException {
//        logger.info("Creating new SOP: {}", sopCreation.getTitle());
//        sopCreation.setCreatedAt(LocalDateTime.now());
//        sopCreation.setApproved(false);
//        sopCreation.setVersion(1);  // Set initial version
//        sopCreation.setStatus(SOPStatus.DRAFT);  // Initially set to draft
//
////        // Attach the SOPInitiation reference
////        SOPInitiation sop = new SOPInitiation();
////        String sopId = sopCreation.getId();
////        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
////                .orElseThrow(() -> new SOPNotFoundException("SOP Initiation not found with id: " + sopId));
////        sopCreation.setSopInitiation(sopInitiation);
//
//        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
//        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
//        return savedSOP;
//    }
//
//    public SOPCreation submitSOPForReview(String id, String updatedBy) throws SOPNotFoundException {
//        SOPCreation sopCreation = getSOPById(id);
//        sopCreation.setStatus(SOPStatus.SUBMITTED);  // Change status to submitted
//        sopCreation.setUpdatedAt(LocalDateTime.now());
//
//        sopCreationRepository.save(sopCreation);
//
//        // Notify reviewers
//      //  emailNotificationService.notifyReviewers(sopCreation);
//
//        return sopCreation;
//    }
//
//    public SOPCreation getSOPById(String id) throws SOPNotFoundException {
//        return sopCreationRepository.findById(id)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
//    }
//
//    @Cacheable(value = "sop_cache", key = "#id")
//    public SOPCreation getCachedSOPById(String id) throws SOPNotFoundException {
//        return getSOPById(id);
//    }
//
//    public List<SOPCreation> getAllSOPs() {
//        return sopCreationRepository.findAll();
//    }
//
//    public SOPCreation updateSOP(String id, SOPCreation updatedSOP) throws SOPNotFoundException {
//        SOPCreation existingSOP = getSOPById(id);
//        existingSOP.setTitle(updatedSOP.getTitle());
//        existingSOP.setDescription(updatedSOP.getDescription());
//        existingSOP.setContent(updatedSOP.getContent());
//        existingSOP.setVersion(existingSOP.getVersion() + 1);  // Increment version on update
//        existingSOP.setUpdatedAt(LocalDateTime.now());
//
//        return sopCreationRepository.save(existingSOP);
//    }
//
//    public void deleteSOP(String id) throws SOPNotFoundException {
//        if (!sopCreationRepository.existsById(id)) {
//            throw new SOPNotFoundException("SOP not found with id: " + id);
//        }
//        sopCreationRepository.deleteById(id);
//    }
//    public SOPCreation getCurrentVersion(String sopReferenceId) {
//        logger.info("Fetching current version of SOP with reference ID: {}", sopReferenceId);
//        SOPCreation currentVersion = sopCreationRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
//                .orElseThrow(() -> {
//                    logger.error("Current version of SOP not found with reference ID: {}", sopReferenceId);
//                    return new SOPNotFoundException("Current version of SOP not found with reference ID: " + sopReferenceId);
//                });
//        return convertToDTO(currentVersion);
//    }
//    public List<SOPCreationDTO> getAllVersions(String sopReferenceId) {
//        logger.info("Fetching all versions of SOP with reference ID: {}", sopReferenceId);
//        List<SOPCreationDTO> versions = sopRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
//        if (versions.isEmpty()) {
//            logger.warn("No versions found for SOP with reference ID: {}", sopReferenceId);
//        }
//        return versions.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    @Transactional
//    public SOPCreation revertToVersion(String sopReferenceId, int version) {
//        logger.info("Reverting SOP with reference ID: {} to version: {}", sopReferenceId, version);
//        try {
//            SOPCreation versionToRevert = getSpecificVersion(sopReferenceId, version);
//            return updateSOP(sopReferenceId, convertToDTO(versionToRevert));
//        } catch (SOPNotFoundException e) {
//            logger.error("SOP or version not found: {}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error reverting SOP: {}", e.getMessage());
//            throw new RuntimeException("Failed to revert SOP", e);
//        }
//    }
//
//    private SOPCreation getSpecificVersion(String sopReferenceId, int version) {
//        return sopRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
//                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
//    }
//
//
//}
//









//// Service
//package com.team.sop_management_service.service;
//import com.team.sop_management_service.dto.SOPCreationDTO;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class SOPCreationService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPService.class);
//    private final SOPCreationRepository sopRepository;
//
//    @Transactional
//    public SOPCreationDTO createSOP(SOPCreationDTO SOPCreationDTO) {
//        logger.info("Creating new SOP: {}", SOPCreationDTO.getTitle());
//        try {
//            SOPCreationDTO sop = convertToEntity(SOPCreationDTO);
//            sop.setSopReferenceId(UUID.randomUUID().toString());
//            sop.setVersion(1);
//            sop.setCreatedBy(String.valueOf(LocalDateTime.now()));
//           // sop.setIsCurrentVersion(true);
//          SOPCreationDTO savedSOP = sopRepository.save(sop);
//            logger.info("SOP created successfully with ID: {}", savedSOP.getId());
//            return convertToDTO(savedSOP);
//        } catch (Exception e) {
//            logger.error("Error creating SOP: {}", e.getMessage());
//            throw new RuntimeException("Failed to create SOP", e);
//        }
//    }
//
//    @Transactional
//    public SOPCreationDTO updateSOP(String sopReferenceId, SOPCreationDTO updatedSOPCreationDTO) {
//        logger.info("Updating SOP with reference ID: {}", sopReferenceId);
//        try {
//            SOPCreationDTO currentVersion = getCurrentVersion(sopReferenceId);
//            //currentVersion.setIsCurrentVersion(false);
//            sopRepository.save(currentVersion);
//
//            SOP updatedSOP = convertToEntity(updatedSOPCreationDTO);
//            updatedSOP.setId(null);
//            updatedSOP.setSopReferenceId(sopReferenceId);
//            updatedSOP.setVersion(currentVersion.getVersion() + 1);
//            updatedSOP.setCreatedAt(LocalDateTime.parse(currentVersion.getCreatedBy()));
//            updatedSOP.setUpdatedAt(LocalDateTime.now());
//            updatedSOP.setIsCurrentVersion(true);
//
//            SOPCreationDTO savedSOP = sopRepository.save(updatedSOP);
//            logger.info("SOP updated successfully. New version: {}", savedSOP.getVersion());
//            return convertToDTO(savedSOP);
//        } catch (SOPNotFoundException e) {
//            logger.error("SOP not found with reference ID: {}", sopReferenceId);
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error updating SOP: {}", e.getMessage());
//            throw new RuntimeException("Failed to update SOP", e);
//        }
//    }
//
//    public SOPCreationDTO getCurrentVersion(String sopReferenceId) {
//        logger.info("Fetching current version of SOP with reference ID: {}", sopReferenceId);
//        SOPCreationDTO currentVersion = sopRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
//                .orElseThrow(() -> {
//                    logger.error("Current version of SOP not found with reference ID: {}", sopReferenceId);
//                    return new SOPNotFoundException("Current version of SOP not found with reference ID: " + sopReferenceId);
//                });
//        return convertToDTO(currentVersion);
//    }
//
//    public List<SOPCreationDTO> getAllVersions(String sopReferenceId) {
//        logger.info("Fetching all versions of SOP with reference ID: {}", sopReferenceId);
//        List<SOPCreationDTO> versions = sopRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
//        if (versions.isEmpty()) {
//            logger.warn("No versions found for SOP with reference ID: {}", sopReferenceId);
//        }
//        return versions.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    @Transactional
//    public SOPCreationDTO revertToVersion(String sopReferenceId, int version) {
//        logger.info("Reverting SOP with reference ID: {} to version: {}", sopReferenceId, version);
//        try {
//            SOPCreationDTO versionToRevert = getSpecificVersion(sopReferenceId, version);
//            return updateSOP(sopReferenceId, convertToDTO(versionToRevert));
//        } catch (SOPNotFoundException e) {
//            logger.error("SOP or version not found: {}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error reverting SOP: {}", e.getMessage());
//            throw new RuntimeException("Failed to revert SOP", e);
//        }
//    }
//
//    private SOPCreationDTO getSpecificVersion(String sopReferenceId, int version) {
//        return sopRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
//                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
//    }
//
//    private SOPCreationDTO convertToDTO(SOPCreationDTO sop) {
//        SOPCreationDTO dto = new SOPCreationDTO();
//        dto.setId(sop.getId());
//        dto.setTitle(sop.getTitle());
//        dto.setDescription(sop.getDescription());
//        dto.setContent(sop.getContent());
//        dto.setVersion(sop.getVersion());
//        dto.setCategory(sop.getCategory());
//        dto.setSubCategory(sop.getSubCategory());
//        dto.setStatus(sop.getStatus());
//        dto.setSopReferenceId(sop.getSopReferenceId());
//        dto.setCreatedBy(sop.getCreatedBy());
//        dto.setUpdatedBy(sop.getUpdatedBy());
//        return dto;
//    }
//
//    private SOPCreationDTO convertToEntity(SOPCreationDTO dto) {
//        SOPCreationDTO sop = new SOPCreationDTO();
//        sop.setTitle(dto.getTitle());
//        sop.setDescription(dto.getDescription());
//        sop.setContent(dto.getContent());
//        sop.setCategory(dto.getCategory());
//        sop.setSubCategory(dto.getSubCategory());
//        sop.setStatus(dto.getStatus());
//        sop.setCreatedBy(dto.getCreatedBy());
//        sop.setUpdatedBy(dto.getUpdatedBy());
//        return sop;
//    }
//}


















package com.team.sop_management_service.service;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SOPCreationService {
    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);
    private final SOPCreationRepository sopCreationRepository;

    public SOPCreation createSOP(SOPCreation sopCreation) {
        logger.info("Creating new SOP: {}", sopCreation.getTitle());
        sopCreation.setCreatedAt(LocalDateTime.now());
        sopCreation.setApproved(false);
        sopCreation.setVersion(1);  // Set initial version
        sopCreation.setStatus(SOPStatus.DRAFT);  // Initially set to draft
        sopCreation.setIsCurrentVersion(true);  // Set as the current version

        // Set the sopReferenceId if it's not already set
        if (sopCreation.getSopReferenceId() == null) {
            sopCreation.setSopReferenceId(java.util.UUID.randomUUID().toString());
        }

        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
        return savedSOP;
    }

    public SOPCreation submitSOPForReview(String id) throws SOPNotFoundException {
        SOPCreation sopCreation = getSOPById(id);
        sopCreation.setStatus(SOPStatus.SUBMITTED);  // Change status to submitted
        sopCreation.setUpdatedAt(LocalDateTime.now());

        sopCreationRepository.save(sopCreation);
        logger.info("SOP submitted for review with ID: {}", id);

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

    public Page<SOPCreation> getAllSOPsPaged(int pageNumber, int pageSize, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return sopCreationRepository.findAll(pageable);
    }

//    @Transactional
//    public SOPCreation updateSOP(String id, SOPCreation updatedSOP) throws SOPNotFoundException {
//        SOPCreation existingSOP = getSOPById(id);
//
//        // Mark existing version as non-current
//        existingSOP.setIsCurrentVersion(false);
//        sopCreationRepository.save(existingSOP);
//
//        // Create a new version
//        SOPCreation newVersion = new SOPCreation();
//
//        // Copy all fields from existing SOP
//        newVersion.setTitle(updatedSOP.getTitle() != null ? updatedSOP.getTitle() : existingSOP.getTitle());
//        newVersion.setDescription(updatedSOP.getDescription() != null ? updatedSOP.getDescription() : existingSOP.getDescription());
//        newVersion.setContent(updatedSOP.getContent() != null ? updatedSOP.getContent() : existingSOP.getContent());
//        newVersion.setCategory(updatedSOP.getCategory() != null ? updatedSOP.getCategory() : existingSOP.getCategory());
//        newVersion.setSubCategory(updatedSOP.getSubCategory() != null ? updatedSOP.getSubCategory() : existingSOP.getSubCategory());
//        newVersion.setStatus(updatedSOP.getStatus() != null ? updatedSOP.getStatus() : existingSOP.getStatus());
//        newVersion.setReviews(updatedSOP.getReviews() != null ? updatedSOP.getReviews() : existingSOP.getReviews());
//        newVersion.setApproved(updatedSOP.getApproved() != null ? updatedSOP.getApproved() : existingSOP.getApproved());
//        newVersion.setSopInitiation(updatedSOP.getSopInitiation() != null ? updatedSOP.getSopInitiation() : existingSOP.getSopInitiation());
//        newVersion.setSopReferenceId(updatedSOP.getSopReferenceId() != null ? updatedSOP.getSopReferenceId() : existingSOP.getSopReferenceId());
//
//        //Set new version specific fields
//        newVersion.setVersion(existingSOP.getVersion() + 1);  // Increment version
//        newVersion.setIsCurrentVersion(true);
//        newVersion.setCreatedAt(existingSOP.getCreatedAt());  // Maintain original creation date
//        newVersion.setUpdatedAt(LocalDateTime.now());
//
//        return sopCreationRepository.save(newVersion);
//    }
@Transactional
public SOPCreation updateSOP(String id, SOPCreation updatedSOP) throws SOPNotFoundException {
    SOPCreation existingSOP = getSOPById(id);

    // Fetch the latest version of the SOP by id
//   SOPCreation existingSOP = sopCreationRepository.findTopBySopReferenceIdOrderByVersionDesc(id);

    if (existingSOP == null) {
        throw new SOPNotFoundException("SOP with ID: " + id + " not found");
    }

    // Mark existing version as non-current
    existingSOP.setIsCurrentVersion(false);
    sopCreationRepository.save(existingSOP);  // Ensure this save happens before creating the new version

    // Create a new version
    SOPCreation newVersion = new SOPCreation();

    // Copy all fields from existing SOP with null checks
    newVersion.setTitle(updatedSOP.getTitle() != null ? updatedSOP.getTitle() : existingSOP.getTitle());
    newVersion.setDescription(updatedSOP.getDescription() != null ? updatedSOP.getDescription() : existingSOP.getDescription());
    newVersion.setContent(updatedSOP.getContent() != null ? updatedSOP.getContent() : existingSOP.getContent());
    newVersion.setCategory(updatedSOP.getCategory() != null ? updatedSOP.getCategory() : existingSOP.getCategory());
    newVersion.setSubCategory(updatedSOP.getSubCategory() != null ? updatedSOP.getSubCategory() : existingSOP.getSubCategory());
    newVersion.setStatus(updatedSOP.getStatus() != null ? updatedSOP.getStatus() : existingSOP.getStatus());
    newVersion.setReviews(updatedSOP.getReviews() != null ? updatedSOP.getReviews() : existingSOP.getReviews());
    newVersion.setApproved(updatedSOP.getApproved() != null ? updatedSOP.getApproved() : existingSOP.getApproved());
    newVersion.setSopInitiation(updatedSOP.getSopInitiation() != null ? updatedSOP.getSopInitiation() : existingSOP.getSopInitiation());
    newVersion.setSopReferenceId(existingSOP.getSopReferenceId());  // Use the same reference ID

    // Set new version specific fields
    newVersion.setVersion(existingSOP.getVersion() + 1);  // Increment version
    newVersion.setIsCurrentVersion(true);  // Mark as the current version
    newVersion.setCreatedAt(existingSOP.getCreatedAt());  // Maintain the original creation date
    newVersion.setUpdatedAt(LocalDateTime.now());  // Set the updated timestamp

    return sopCreationRepository.save(newVersion);  // Save the new version
}

    public void deleteSOP(String id) throws SOPNotFoundException {
        if (!sopCreationRepository.existsById(id)) {
            throw new SOPNotFoundException("SOP not found with id: " + id);
        }
        sopCreationRepository.deleteById(id);
    }

    public SOPCreation getCurrentVersion(String sopReferenceId) throws SOPNotFoundException {
        logger.info("Fetching current version of SOP with reference ID: {}", sopReferenceId);
        return sopCreationRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
                .orElseThrow(() -> new SOPNotFoundException("Current version of SOP not found with reference ID: " + sopReferenceId));
    }

    public List<SOPCreation> getAllVersions(String sopReferenceId) {
        logger.info("Fetching all versions of SOP with reference ID: {}", sopReferenceId);
        List<SOPCreation> versions = sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
        if (versions.isEmpty()) {
            logger.warn("No versions found for SOP with reference ID: {}", sopReferenceId);
        }
        return versions;
    }

    public Page<SOPCreation> getAllVersionsPaged(String sopReferenceId, int pageNumber, int pageSize) {
        logger.info("Fetching paged versions of SOP with reference ID: {}", sopReferenceId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("version").descending());
        return sopCreationRepository.findBySopReferenceId(sopReferenceId, pageable);
    }

    @Transactional
    public SOPCreation revertToVersion(String sopReferenceId, int version) throws SOPNotFoundException {
        logger.info("Reverting SOP with reference ID: {} to version: {}", sopReferenceId, version);

        SOPCreation versionToRevert = getSpecificVersion(sopReferenceId, version);

        // Mark all versions as non-current
        List<SOPCreation> versions = sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
        versions.forEach(v -> v.setIsCurrentVersion(false));
        sopCreationRepository.saveAll(versions);

        // Set the desired version as the current version
        versionToRevert.setIsCurrentVersion(true);
        sopCreationRepository.save(versionToRevert);

        logger.info("SOP reverted to version: {} with reference ID: {}", version, sopReferenceId);
        return versionToRevert;
    }

    private SOPCreation getSpecificVersion(String sopReferenceId, int version) throws SOPNotFoundException {
        return sopCreationRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
    }
//    // Method to get all SOPs with pagination
//    public Page<SOPCreation> getAllVersionsPaged(Pageable pageable) {
//        // Fetch SOP records with pagination
//        return sopCreationRepository.findAll(pageable);
//    }'

        // Method to get all SOPs with pagination
    public Page<SOPCreation> getAllVersionsPaged(Pageable pageable) {
        // Fetch SOP records with pagination
        return sopCreationRepository.findAll(pageable);
    }

    // Update the status of an SOP
    public void updateSOPStatus(String sopId, SOPStatus newStatus) {
        SOPCreation sop = sopCreationRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));

        // Update the status and timestamp
        sop.setStatus(newStatus);
        sop.setUpdatedAt(LocalDateTime.now());

        // Save the updated SOP
        sopCreationRepository.save(sop);
    }

//    public Page<SOPCreation> getAllVersionsPaged(Pageable pageable) {
//    }

//    public Page<SOPCreation> getAllSOPsPaged(Pageable pageable) {
//    }
}

