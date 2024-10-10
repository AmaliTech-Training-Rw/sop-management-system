//package com.team.sop_management_service.service;
//
//
//// Model
//package com.team.sop_management_service.model;
//
//import com.team.sop_management_service.enums.SOPStatus;
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@Document(collection = "sop_documents")
//public class SOP {
//    @Id
//    private String id;
//    private String title;
//    private String description;
//    private String content;
//    private Integer version;
//    private String category;
//    private String subCategory;
//    private SOPStatus status;
//    private List<String> reviews;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private Boolean approved;
//    private String sopReferenceId;
//    private String createdBy;
//    private String updatedBy;
//    private Boolean isCurrentVersion;
//}
//
//// Repository
//package com.team.sop_management_service.repository;
//
//import com.team.sop_management_service.model.SOP;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SOPRepository extends MongoRepository<SOP, String> {
//    List<SOP> findBySopReferenceIdOrderByVersionDesc(String sopReferenceId);
//    Optional<SOP> findBySopReferenceIdAndVersion(String sopReferenceId, int version);
//    Optional<SOP> findBySopReferenceIdAndIsCurrentVersionTrue(String sopReferenceId);
//}
//
//// Service
//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.dto.SOPDTO;
//import com.team.sop_management_service.model.SOP;
//import com.team.sop_management_service.repository.SOPRepository;
//import com.team.sop_management_service.exception.SOPNotFoundException;
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
//public class SOPService {
//    private static final Logger logger = LoggerFactory.getLogger(SOPService.class);
//    private final SOPRepository sopRepository;
//
//    @Transactional
//    public SOPDTO createSOP(SOPDTO sopDTO) {
//        logger.info("Creating new SOP: {}", sopDTO.getTitle());
//        try {
//            SOP sop = convertToEntity(sopDTO);
//            sop.setSopReferenceId(UUID.randomUUID().toString());
//            sop.setVersion(1);
//            sop.setCreatedAt(LocalDateTime.now());
//            sop.setIsCurrentVersion(true);
//            SOP savedSOP = sopRepository.save(sop);
//            logger.info("SOP created successfully with ID: {}", savedSOP.getId());
//            return convertToDTO(savedSOP);
//        } catch (Exception e) {
//            logger.error("Error creating SOP: {}", e.getMessage());
//            throw new RuntimeException("Failed to create SOP", e);
//        }
//    }
//
//    @Transactional
//    public SOPDTO updateSOP(String sopReferenceId, SOPDTO updatedSOPDTO) {
//        logger.info("Updating SOP with reference ID: {}", sopReferenceId);
//        try {
//            SOP currentVersion = getCurrentVersion(sopReferenceId);
//            currentVersion.setIsCurrentVersion(false);
//            sopRepository.save(currentVersion);
//
//            SOP updatedSOP = convertToEntity(updatedSOPDTO);
//            updatedSOP.setId(null);
//            updatedSOP.setSopReferenceId(sopReferenceId);
//            updatedSOP.setVersion(currentVersion.getVersion() + 1);
//            updatedSOP.setCreatedAt(currentVersion.getCreatedAt());
//            updatedSOP.setUpdatedAt(LocalDateTime.now());
//            updatedSOP.setIsCurrentVersion(true);
//
//            SOP savedSOP = sopRepository.save(updatedSOP);
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
//    public SOPDTO getCurrentVersion(String sopReferenceId) {
//        logger.info("Fetching current version of SOP with reference ID: {}", sopReferenceId);
//        SOP currentVersion = sopRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
//                .orElseThrow(() -> {
//                    logger.error("Current version of SOP not found with reference ID: {}", sopReferenceId);
//                    return new SOPNotFoundException("Current version of SOP not found with reference ID: " + sopReferenceId);
//                });
//        return convertToDTO(currentVersion);
//    }
//
//    public List<SOPDTO> getAllVersions(String sopReferenceId) {
//        logger.info("Fetching all versions of SOP with reference ID: {}", sopReferenceId);
//        List<SOP> versions = sopRepository.findBySopReferenceIdOrderByVersionDesc(sopReferenceId);
//        if (versions.isEmpty()) {
//            logger.warn("No versions found for SOP with reference ID: {}", sopReferenceId);
//        }
//        return versions.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    @Transactional
//    public SOPDTO revertToVersion(String sopReferenceId, int version) {
//        logger.info("Reverting SOP with reference ID: {} to version: {}", sopReferenceId, version);
//        try {
//            SOP versionToRevert = getSpecificVersion(sopReferenceId, version);
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
//    private SOP getCurrentVersion(String sopReferenceId) {
//        return sopRepository.findBySopReferenceIdAndIsCurrentVersionTrue(sopReferenceId)
//                .orElseThrow(() -> new SOPNotFoundException("Current version not found for SOP with reference ID: " + sopReferenceId));
//    }
//
//    private SOP getSpecificVersion(String sopReferenceId, int version) {
//        return sopRepository.findBySopReferenceIdAndVersion(sopReferenceId, version)
//                .orElseThrow(() -> new SOPNotFoundException("Version " + version + " not found for SOP with reference ID: " + sopReferenceId));
//    }
//
//    private SOPDTO convertToDTO(SOP sop) {
//        SOPDTO dto = new SOPDTO();
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
//    private SOP convertToEntity(SOPDTO dto) {
//        SOP sop = new SOP();
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
//
//
//// Unit Test
//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.dto.SOPDTO;
//import com.team.sop_management_service.model.SOP;
//import com.team.sop_management_service.repository.SOPRepository;
//import com.team.sop_management_service.enums.SOPStatus;
//import com.team.sop_management_service.exception.SOPNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//        import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class SOPServiceTest {
//
//    @Mock
//    private SOPRepository sopRepository;
//
//    @InjectMocks
//    private SOPService sopService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createSOP_Success() {
//        SOPDTO inputDTO = new SOPDTO();
//        inputDTO.setTitle("Test SOP");
//        inputDTO.setContent("Test Content");
//        inputDTO.setStatus(SOPStatus.DRAFT);
//
//        SOP savedSOP = new SOP();
//        savedSOP.setId("testId");
//        savedSOP.setTitle("Test SOP");
//        savedSOP.setContent("Test Content");
//        savedSOP.setStatus(SOPStatus.DRAFT);
//        savedSOP.setVersion(1);
//
//        when(sopRepository.save(any(SOP.class))).thenReturn(savedSOP);
//
//        SOPDTO result = sopService.createSOP(inputDTO);
//
//        assertNotNull(result);
//        assertEquals("Test SOP", result.getTitle());
//        assertEquals(1, result.getVersion());
//        verify(sopRepository, times(1)).save(any(SOP.class));
//    }
//
//    @Test
//    void updateSOP_Success() {
//        String sopReferenceId = "test