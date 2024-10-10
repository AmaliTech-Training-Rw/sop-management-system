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

    public SOPCreation createSOP(SOPCreation sopCreation) throws SOPNotFoundException {
        logger.info("Creating new SOP: {}", sopCreation.getTitle());
        sopCreation.setCreatedAt(LocalDateTime.now());
        sopCreation.setApproved(false);
        sopCreation.setVersion(1);  // Set initial version
        sopCreation.setStatus(SOPStatus.DRAFT);  // Initially set to draft

//        // Attach the SOPInitiation reference
//        SOPInitiation sop = new SOPInitiation();
//        String sopId = sopCreation.getId();
//        SOPInitiation sopInitiation = sopInitiationRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP Initiation not found with id: " + sopId));
//        sopCreation.setSopInitiation(sopInitiation);

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
