package com.team.sop_management_service.service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

        SOPCreation sopCreation = SOPCreation.builder()
                .title(sopCreationDTO.getTitle())
                .description(sopCreationDTO.getDescription())
                .content(sopCreationDTO.getContent())
                .category(sopCreationDTO.getCategory())
                .subCategory(sopCreationDTO.getSubCategory())
                .createdAt(LocalDateTime.now())
                .approved(false)
                .version(1)
                .status(SOPStatus.DRAFT)
                .isCurrentVersion(true)
                .sopReferenceId(sopCreationDTO.getSopReferenceId() != null ? sopCreationDTO.getSopReferenceId() : UUID.randomUUID().toString())
                .sopInitiation(sopInitiationService.getSOPById(sopCreationDTO.getSopInitiationId()))
                .reviews(fetchReviewerIds(sopCreationDTO.getReviewUserIds()))
                .build();

        SOPCreation savedSOP = sopCreationRepository.save(sopCreation);
        logger.info("SOP created successfully with ID: {}", savedSOP.getId());
        return savedSOP;
    }

    public SOPCreation submitSOPForReview(String id) throws SOPNotFoundException {
        SOPCreation sopCreation = getSOPById(id);

        // Check if the SOP is the current version
        if (!sopCreation.getIsCurrentVersion()) {
            throw new IllegalStateException("Only the current version can be submitted for review.");
        }

        // Proceed with submission
        sopCreation = sopCreation.toBuilder()
                .status(SOPStatus.SUBMITTED)
                .updatedAt(LocalDateTime.now())
                .build();

        sopCreationRepository.save(sopCreation);
        logger.info("SOP submitted for review with ID: {}", id);

        return sopCreation;
    }
    public SOPCreation undoSubmission(String id) throws SOPNotFoundException {
        SOPCreation sopCreation = getSOPById(id);

        // Check if the SOP is in the SUBMITTED status
        if (sopCreation.getStatus() != SOPStatus.SUBMITTED) {
            throw new IllegalStateException("Only a submitted SOP can be reverted.");
        }

        // Revert to draft status
        sopCreation = sopCreation.toBuilder()
                .status(SOPStatus.DRAFT)
                .updatedAt(LocalDateTime.now())
                .build();

        sopCreationRepository.save(sopCreation);
        logger.info("SOP submission undone for SOP with ID: {}", id);

        return sopCreation;
    }

//
//    @Transactional
//    public SOPCreation updateSOP(String id, SOPCreationDTO updatedSOPDTO) throws SOPNotFoundException {
//        SOPCreation existingSOP = getSOPById(id);
//
//        if (existingSOP == null) {
//            throw new SOPNotFoundException("SOP with ID: " + id + " not found");
//        }
//
//        existingSOP = existingSOP.toBuilder()
//                .isCurrentVersion(false)
//                .build();
//        sopCreationRepository.save(existingSOP);
//
//        SOPCreation newVersion = SOPCreation.builder()
//                .title(updatedSOPDTO.getTitle())
//                .description(updatedSOPDTO.getDescription())
//                .content(updatedSOPDTO.getContent())
//                .category(updatedSOPDTO.getCategory())
//                .subCategory(updatedSOPDTO.getSubCategory())
//                .sopReferenceId(existingSOP.getSopReferenceId())
//                .version(existingSOP.getVersion() + 1)
//                .isCurrentVersion(true)
//                .createdAt(existingSOP.getCreatedAt())
//                .updatedAt(LocalDateTime.now())
//                .sopInitiation(sopInitiationService.getSOPById(updatedSOPDTO.getSopInitiationId()))
//                .reviews(fetchReviewers(updatedSOPDTO.getReviewUserIds()))
//                .build();
//
//        return sopCreationRepository.save(newVersion);
//    }
//    private List<UserDto> fetchReviewers(List<Integer> reviewUserIds) {
//        if (reviewUserIds == null || reviewUserIds.isEmpty()) {
//            return List.of();
//        }
//
//        return reviewUserIds.stream()
//                .map(userId -> {
//                    ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);
//
//                    if (response.getStatusCode().is2xxSuccessful()) {
//                        return response.getBody();
//                    } else {
//                        throw new RuntimeException("Failed to fetch user with ID: " + userId);
//                    }
//                })
//                .collect(Collectors.toList()).reversed();
//    }
@Transactional
public SOPCreation updateSOP(String id, SOPCreationDTO updatedSOPDTO) throws SOPNotFoundException {
    SOPCreation existingSOP = getSOPById(id);

    if (existingSOP == null) {
        throw new SOPNotFoundException("SOP with ID: " + id + " not found");
    }

    existingSOP = existingSOP.toBuilder()
            .isCurrentVersion(false)
            .build();
    sopCreationRepository.save(existingSOP);

    SOPCreation newVersion = SOPCreation.builder()
            .title(updatedSOPDTO.getTitle())
            .description(updatedSOPDTO.getDescription())
            .content(updatedSOPDTO.getContent())
            .category(updatedSOPDTO.getCategory())
            .subCategory(updatedSOPDTO.getSubCategory())
            .sopReferenceId(existingSOP.getSopReferenceId())
            .version(existingSOP.getVersion() + 1)
            .isCurrentVersion(true)
            .createdAt(existingSOP.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .sopInitiation(sopInitiationService.getSOPById(updatedSOPDTO.getSopInitiationId()))
            .reviews(fetchReviewerIds(updatedSOPDTO.getReviewUserIds())) // Changed method name
            .build();

    return sopCreationRepository.save(newVersion);
}

    private List<Integer> fetchReviewerIds(List<Integer> reviewUserIds) {
        if (reviewUserIds == null || reviewUserIds.isEmpty()) {
            return List.of();
        }

        return reviewUserIds.stream()
                .map(userId -> {
                    ResponseEntity<UserDto> response = authenticationServiceClient.getUserById(userId);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        return userId; // Return the ID instead of the UserDto
                    } else {
                        throw new RuntimeException("Failed to fetch user with ID: " + userId);
                    }
                })
                .collect(Collectors.toList());
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
    public SOPCreation getSOPById(String id) throws SOPNotFoundException {
        return sopCreationRepository.findById(id)
                .orElseThrow(() -> new SOPNotFoundException("SOP not found with id: " + id));
    }
    public Page<SOPCreation> getAllSOPsPaged(int pageNumber, int pageSize, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return sopCreationRepository.findAll(pageable);
    }
}