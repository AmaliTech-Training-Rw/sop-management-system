//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.enums.SOPStatus;
//import com.team.sop_management_service.error.SOPNotFoundException;
//import com.team.sop_management_service.models.SOPCreation;
//import com.team.sop_management_service.repository.SOPCreationRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class SOPCreationService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPCreationService.class);
//    private final SOPCreationRepository sopCreationRepository;
//
//    public SOPCreation createSOP(SOPCreation sopCreation) {
//        logger.info("Creating new SOP: {}", sopCreation.getTitle());
//        sopCreation.setCreatedAt(LocalDateTime.now());
//        sopCreation.setApproved(false);
//        sopCreation.setVersion(1);  // Set initial version
//        sopCreation.setStatus(SOPStatus.DRAFT);  // Initially set to draft
//        sopCreation.setIsCurrentVersion(true);  // Set as the current version
//
//        // Set the sopReferenceId if it's not already set
//        if (sopCreation.getSopReferenceId() == null) {
//            sopCreation.setSopReferenceId(java.util.UUID.randomUUID().toString());
//        }
//
//        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
//        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
//        return savedSOP;
//    }
//
//    public SOPCreation submitSOPForReview(String id) throws SOPNotFoundException {
//        SOPCreation sopCreation = getSOPById(id);
//        sopCreation.setStatus(SOPStatus.SUBMITTED);  // Change status to submitted
//        sopCreation.setUpdatedAt(LocalDateTime.now());
//
//        sopCreationRepository.save(sopCreation);
//        logger.info("SOP submitted for review with ID: {}", id);
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
//    public Page<SOPCreation> getAllSOPsPaged(int pageNumber, int pageSize, String sortBy, String direction) {
//        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
//        return sopCreationRepository.findAll(pageable);
//    }
//
//
//    @Transactional
//    public SOPCreation updateSOP(String id, SOPCreation updatedSOP) throws SOPNotFoundException {
//     // Fetch the latest version of the SOP by id
//         SOPCreation existingSOP = getSOPById(id);
//
//         if (existingSOP == null) {
//            throw new SOPNotFoundException("SOP with ID: " + id + " not found");
//         }
//
//        // Mark existing version as non-current
//        existingSOP.setIsCurrentVersion(false);
//        sopCreationRepository.save(existingSOP);  // Ensure this save happens before creating the new version
//
//        // Create a new version
//        SOPCreation newVersion = new SOPCreation();
//
//        // Copy all fields from existing SOP with null checks
//        newVersion.setTitle(updatedSOP.getTitle() != null ? updatedSOP.getTitle() : existingSOP.getTitle());
//        newVersion.setDescription(updatedSOP.getDescription() != null ? updatedSOP.getDescription() : existingSOP.getDescription());
//        newVersion.setContent(updatedSOP.getContent() != null ? updatedSOP.getContent() : existingSOP.getContent());
//        newVersion.setCategory(updatedSOP.getCategory() != null ? updatedSOP.getCategory() : existingSOP.getCategory());
//        newVersion.setSubCategory(updatedSOP.getSubCategory() != null ? updatedSOP.getSubCategory() : existingSOP.getSubCategory());
//        newVersion.setStatus(updatedSOP.getStatus() != null ? updatedSOP.getStatus() : existingSOP.getStatus());
//        newVersion.setReviews(updatedSOP.getReviews() != null ? updatedSOP.getReviews() : existingSOP.getReviews());
//        newVersion.setApproved(updatedSOP.getApproved() != null ? updatedSOP.getApproved() : existingSOP.getApproved());
//        newVersion.setSopInitiation(updatedSOP.getSopInitiation() != null ? updatedSOP.getSopInitiation() : existingSOP.getSopInitiation());
//        newVersion.setSopReferenceId(existingSOP.getSopReferenceId());  // Use the same reference ID
//
//        // Set new version specific fields
//        newVersion.setVersion(existingSOP.getVersion() + 1);  // Increment version
//        newVersion.setIsCurrentVersion(true);  // Mark as the current version
//        newVersion.setCreatedAt(existingSOP.getCreatedAt());  // Maintain the original creation date
//        newVersion.setUpdatedAt(LocalDateTime.now());  // Set the updated timestamp
//
//        return sopCreationRepository.save(newVersion);  // Save the new version
//    }
//
//    public void deleteSOP(String id) throws SOPNotFoundException {
//        if (!sopCreationRepository.existsById(id)) {
//            throw new SOPNotFoundException("SOP not found with id: " + id);
//        }
//        sopCreationRepository.deleteById(id);
//    }
//
//    public SOPCreation getCurrentVersion(String sopReferenceId) throws SOPNotFoundException {
//        logger.info("Fetching current version of SOP with reference ID: {}", sopReferenceId);
//        return sopCreationRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
//                .orElseThrow(() -> new SOPNotFoundException("Current version of SOP not found with reference ID: " + sopReferenceId));
//    }
//
//    public List<SOPCreation> getAllVersions(String sopReferenceId) {
//        logger.info("Fetching all versions of SOP with reference ID: {}", sopReferenceId);
//        List<SOPCreation> versions = sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
//        if (versions.isEmpty()) {
//            logger.warn("No versions found for SOP with reference ID: {}", sopReferenceId);
//        }
//        return versions;
//    }
//
//    public Page<SOPCreation> getAllVersionsPaged(String sopReferenceId, int pageNumber, int pageSize) {
//        logger.info("Fetching paged versions of SOP with reference ID: {}", sopReferenceId);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("version").descending());
//        return sopCreationRepository.findBySopReferenceId(sopReferenceId, pageable);
//    }
//
//    @Transactional
//    public SOPCreation revertToVersion(String sopReferenceId, int version) throws SOPNotFoundException {
//        logger.info("Reverting SOP with reference ID: {} to version: {}", sopReferenceId, version);
//
//        SOPCreation versionToRevert = getSpecificVersion(sopReferenceId, version);
//
//        // Mark all versions as non-current
//        List<SOPCreation> versions = sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
//        versions.forEach(v -> v.setIsCurrentVersion(false));
//        sopCreationRepository.saveAll(versions);
//
//        // Set the desired version as the current version
//        versionToRevert.setIsCurrentVersion(true);
//        sopCreationRepository.save(versionToRevert);
//
//        logger.info("SOP reverted to version: {} with reference ID: {}", version, sopReferenceId);
//        return versionToRevert;
//    }
//
//    private SOPCreation getSpecificVersion(String sopReferenceId, int version) throws SOPNotFoundException {
//        return sopCreationRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
//                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
//    }
////    // Method to get all SOPs with pagination
////    public Page<SOPCreation> getAllVersionsPaged(Pageable pageable) {
////        // Fetch SOP records with pagination
////        return sopCreationRepository.findAll(pageable);
////    }'
//
//        // Method to get all SOPs with pagination
//    public List<SOPCreation> getAllVersionsPaged() {
//        // Fetch SOP records with pagination
//        return sopCreationRepository.findAll();
//    }
//
//    // Update the status of an SOP
//    public void updateSOPStatus(String sopId, SOPStatus newStatus) {
//        SOPCreation sop = sopCreationRepository.findById(sopId)
//                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));
//
//        // Update the status and timestamp
//        sop.setStatus(newStatus);
//        sop.setUpdatedAt(LocalDateTime.now());
//
//        // Save the updated SOP
//        sopCreationRepository.save(sop);
//    }
//
////    public Page<SOPCreation> getAllVersionsPaged(Pageable pageable) {
////    }
//
////    public Page<SOPCreation> getAllSOPsPaged(Pageable pageable) {
////    }
//}
//


package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.dto.SOPInitiationDTO;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
    private final AuthenticationServiceClient authenticationServiceClient;
    private final SOPInitiationService sopInitiationService;

    public SOPCreation createSOP(SOPCreationDTO sopCreationDTO) {
        logger.info("Creating new SOP: {}", sopCreationDTO.getTitle());
        SOPCreation sopCreation = convertDTOToEntity(sopCreationDTO);
        sopCreation.setCreatedAt(LocalDateTime.now());
        sopCreation.setApproved(false);
        sopCreation.setVersion(1);
        sopCreation.setStatus(SOPStatus.DRAFT);
        sopCreation.setIsCurrentVersion(true);

        if (sopCreation.getSopReferenceId() == null) {
            sopCreation.setSopReferenceId(java.util.UUID.randomUUID().toString());
        }

        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
        return savedSOP;
    }

    public SOPCreation submitSOPForReview(String id) throws SOPNotFoundException {
        SOPCreation sopCreation = getSOPById(id);
        sopCreation.setStatus(SOPStatus.SUBMITTED);
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

    @Transactional
    public SOPCreation updateSOP(String id, SOPCreationDTO updatedSOPDTO) throws SOPNotFoundException {
        SOPCreation existingSOP = getSOPById(id);

        if (existingSOP == null) {
            throw new SOPNotFoundException("SOP with ID: " + id + " not found");
        }

        existingSOP.setIsCurrentVersion(false);
        sopCreationRepository.save(existingSOP);

        SOPCreation newVersion = convertDTOToEntity(updatedSOPDTO);
        newVersion.setSopReferenceId(existingSOP.getSopReferenceId());
        newVersion.setVersion(existingSOP.getVersion() + 1);
        newVersion.setIsCurrentVersion(true);
        newVersion.setCreatedAt(existingSOP.getCreatedAt());
        newVersion.setUpdatedAt(LocalDateTime.now());

        return sopCreationRepository.save(newVersion);
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

        List<SOPCreation> versions = sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
        versions.forEach(v -> v.setIsCurrentVersion(false));
        sopCreationRepository.saveAll(versions);

        versionToRevert.setIsCurrentVersion(true);
        sopCreationRepository.save(versionToRevert);

        logger.info("SOP reverted to version: {} with reference ID: {}", version, sopReferenceId);
        return versionToRevert;
    }

    private SOPCreation getSpecificVersion(String sopReferenceId, int version) throws SOPNotFoundException {
        return sopCreationRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
    }

    public List<SOPCreation> getAllVersionsPaged() {
        return sopCreationRepository.findAll();
    }

    public void updateSOPStatus(String sopId, SOPStatus newStatus) {
        SOPCreation sop = sopCreationRepository.findById(sopId)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found"));

        sop.setStatus(newStatus);
        sop.setUpdatedAt(LocalDateTime.now());

        sopCreationRepository.save(sop);
    }

    private SOPCreation convertDTOToEntity(SOPCreationDTO dto) {
        SOPCreation entity = new SOPCreation();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setVersion(dto.getVersion());
        entity.setCategory(dto.getCategory());
        entity.setSubCategory(dto.getSubCategory());
        entity.setStatus(dto.getStatus());
        entity.setApproved(dto.getApproved());
        entity.setSopReferenceId(dto.getSopReferenceId());
        entity.setIsCurrentVersion(dto.getIsCurrentVersion());

        // Fetch SOPInitiation entity based on ID
        SOPInitiation sopInitiation = sopInitiationService.getSOPById(dto.getSopInitiationId());
        entity.setSopInitiation(sopInitiation);

        // Fetch reviewers from the authentication service and set them
        if (dto.getReviewUserIds() != null && !dto.getReviewUserIds().isEmpty()) {
            List<UserDto> reviewers = dto.getReviewUserIds().stream()
                    .map(userId -> {
                        ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            return response.getBody(); // Return the UserDto
                        } else {
                            // Handle error response, e.g., return null or throw an exception
                            throw new RuntimeException("Failed to fetch user with ID: " + userId);
                        }
                    })
                    .collect(Collectors.toList());

            entity.setReviews(reviewers);
        }

        return entity;
    }

    private SOPCreationDTO convertEntityToDTO(SOPCreation entity) {
        SOPCreationDTO dto = new SOPCreationDTO();
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        dto.setVersion(entity.getVersion());
        dto.setCategory(entity.getCategory());
        dto.setSubCategory(entity.getSubCategory());
        dto.setStatus(entity.getStatus());
        dto.setApproved(entity.getApproved());
        dto.setSopReferenceId(entity.getSopReferenceId());
        dto.setIsCurrentVersion(entity.getIsCurrentVersion());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Set the SOPInitiation ID
        if (entity.getSopInitiation() != null) {
            dto.setSopInitiationId(entity.getSopInitiation().getSopId());
        }

//        // Map reviewers from the entity to the DTO
//        if (entity.getReviews() != null) {
//            dto.setReviewUserIds(entity.getReviews().stream()
//                    .map(UserDto::getId)
//                    .collect(Collectors.toList()).reversed());
//        }

        return dto;
    }
}