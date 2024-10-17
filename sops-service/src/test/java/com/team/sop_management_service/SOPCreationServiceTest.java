package com.team.sop_management_service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.exceptions.SOPNotFoundException;
import com.team.sop_management_service.models.SOPCreation;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.service.SOPCreationService;
import com.team.sop_management_service.service.SOPInitiationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class SOPCreationServiceTest {

    @Mock
    private SOPCreationRepository sopCreationRepository;

    @Mock
    private AuthenticationServiceClient authenticationServiceClient;

    @Mock
    private SOPInitiationService sopInitiationService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SOPCreationService sopCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSOP_ValidInput_Success() {
        // Arrange
        SOPCreationDTO dto = new SOPCreationDTO();
        dto.setTitle("Test SOP");
        dto.setSopInitiationId("initId");
        dto.setReviewUserIds(Arrays.asList(1, 2));

        SOPInitiation sopInitiation = new SOPInitiation();
        when(sopInitiationService.getSOPById("initId")).thenReturn(sopInitiation);
        when(authenticationServiceClient.getUserById(anyInt())).thenReturn(ResponseEntity.ok(new UserDto()));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SOPCreation result = sopCreationService.createSOP(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test SOP", result.getTitle());
        assertEquals(sopInitiation, result.getSopInitiation());
        assertEquals(2, result.getReviews().size());
        verify(sopCreationRepository).save(any(SOPCreation.class));
    }

    @Test
    void submitSOPForReview_ValidSOP_Success() throws SOPNotFoundException {
        // Arrange
        String sopId = "sopId";
        SOPCreation sop = new SOPCreation();
        sop.setId(sopId);
        sop.setIsCurrentVersion(true);
        sop.setStatus(SOPStatus.DRAFT);

        when(sopCreationRepository.findById(sopId)).thenReturn(Optional.of(sop));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SOPCreation result = sopCreationService.submitSOPForReview(sopId);

        // Assert
        assertEquals(SOPStatus.SUBMITTED, result.getStatus());
        verify(notificationService).notifyAuthorOfReviewerComment(any(SOPCreation.class));
    }

    @Test
    void undoSubmission_ValidSOP_Success() throws SOPNotFoundException {
        // Arrange
        String sopId = "sopId";
        SOPCreation sop = new SOPCreation();
        sop.setId(sopId);
        sop.setStatus(SOPStatus.SUBMITTED);

        when(sopCreationRepository.findById(sopId)).thenReturn(Optional.of(sop));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SOPCreation result = sopCreationService.undoSubmission(sopId);

        // Assert
        assertEquals(SOPStatus.DRAFT, result.getStatus());
    }

    @Test
    void updateSOP_ValidInput_Success() throws SOPNotFoundException {
        // Arrange
        String sopId = "sopId";
        SOPCreationDTO dto = new SOPCreationDTO();
        dto.setTitle("Updated SOP");

        SOPCreation existingSOP = new SOPCreation();
        existingSOP.setId(sopId);
        existingSOP.setVersion(1);
        existingSOP.setSopReferenceId(UUID.randomUUID().toString());

        when(sopCreationRepository.findById(sopId)).thenReturn(Optional.of(existingSOP));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SOPCreation result = sopCreationService.updateSOP(sopId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated SOP", result.getTitle());
        assertEquals(2, result.getVersion());
        assertTrue(result.getIsCurrentVersion());
    }

    @Test
    void getCurrentVersion_ValidReferenceId_Success() throws SOPNotFoundException {
        // Arrange
        String refId = "refId";
        SOPCreation currentVersion = new SOPCreation();
        when(sopCreationRepository.findBySopReferenceIdAndIsCurrentVersionTrue(refId)).thenReturn(Optional.of(currentVersion));

        // Act
        SOPCreation result = sopCreationService.getCurrentVersion(refId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getAllVersionsPaged_ValidInput_Success() {
        // Arrange
        String refId = "refId";
        Pageable pageable = PageRequest.of(0, 10);
        List<SOPCreation> versions = Arrays.asList(new SOPCreation(), new SOPCreation());
        Page<SOPCreation> page = new PageImpl<>(versions);

        when(sopCreationRepository.findBySopReferenceId(refId, pageable)).thenReturn(page);

        // Act
        Page<SOPCreation> result = sopCreationService.getAllVersionsPaged(refId, 0, 10);

        // Assert
        assertEquals(2, result.getContent().size());
    }

    @Test
    void revertToVersion_ValidInput_Success() throws SOPNotFoundException {
        // Arrange
        String refId = "refId";
        int version = 1;
        SOPCreation versionToRevert = new SOPCreation();
        versionToRevert.setVersion(version);

        when(sopCreationRepository.findBySopReferenceIdAndVersion(refId, version)).thenReturn(Optional.of(versionToRevert));
        when(sopCreationRepository.findBySopReferenceIdOrderByVersionDesc(refId)).thenReturn(Arrays.asList(versionToRevert));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SOPCreation result = sopCreationService.revertToVersion(refId, version);

        // Assert
        assertTrue(result.getIsCurrentVersion());
        assertEquals(version, result.getVersion());
    }

    @Test
    void updateSOPStatus_ValidInput_Success() {
        // Arrange
        String sopId = "sopId";
        SOPCreation sop = new SOPCreation();
        sop.setId(sopId);
        sop.setStatus(SOPStatus.DRAFT);

        when(sopCreationRepository.findById(sopId)).thenReturn(Optional.of(sop));
        when(sopCreationRepository.save(any(SOPCreation.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        sopCreationService.updateSOPStatus(sopId, SOPStatus.APPROVED);

        // Assert
        assertEquals(SOPStatus.APPROVED, sop.getStatus());
        assertNotNull(sop.getUpdatedAt());
    }

    @Test
    void getAllSOPsPaged_ValidInput_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<SOPCreation> sops = Arrays.asList(new SOPCreation(), new SOPCreation());
        Page<SOPCreation> page = new PageImpl<>(sops);

        when(sopCreationRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Page<SOPCreation> result = sopCreationService.getAllSOPsPaged(0, 10, "createdAt", "DESC");

        // Assert
        assertEquals(2, result.getContent().size());
    }
}