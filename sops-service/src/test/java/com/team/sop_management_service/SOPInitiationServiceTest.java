package com.team.sop_management_service;

import com.team.sop_management_service.authenticationService.AuthenticationServiceClient;
import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.dto.SOPInitiationDTO;
import com.team.sop_management_service.exceptions.InvalidSOPException;
import com.team.sop_management_service.exceptions.SOPNotFoundException;
import com.team.sop_management_service.models.ApprovalPipeline;
import com.team.sop_management_service.models.SOPInitiation;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.service.SOPInitiationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class SOPInitiationServiceTest {

    @Mock
    private SOPInitiationRepository sopRepository;

    @Mock
    private AuthenticationServiceClient authenticationServiceClient;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SOPInitiationService sopInitiationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initiateSOP_ValidInput_Success() {
        // Arrange
        SOPInitiationDTO inputDTO = createValidSOPInitiationDTO();
        SOPInitiation savedSOP = createSavedSOPInitiation();

        when(authenticationServiceClient.getUserById(anyInt())).thenReturn(ResponseEntity.ok(new UserDto()));
        when(sopRepository.save(any(SOPInitiation.class))).thenReturn(savedSOP);

        // Act
        SOPInitiationDTO result = sopInitiationService.initiateSOP(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(savedSOP.getSopId(), result.getSopId());
        assertEquals(savedSOP.getTitle(), result.getTitle());
        assertEquals(savedSOP.getVisibility(), result.getVisibility());

        verify(notificationService).notifyAuthor(any(SOPInitiation.class));
    }

    @Test
    void initiateSOP_InvalidInput_ThrowsException() {
        // Arrange
        SOPInitiationDTO invalidDTO = new SOPInitiationDTO(); // Empty DTO

        // Act & Assert
        assertThrows(InvalidSOPException.class, () -> sopInitiationService.initiateSOP(invalidDTO));
    }

    @Test
    void getAllSOPs_ReturnsListOfSOPs() {
        // Arrange
        List<SOPInitiation> sopList = Arrays.asList(createSavedSOPInitiation(), createSavedSOPInitiation());
        when(sopRepository.findAll()).thenReturn(sopList);

        // Act
        List<SOPInitiationDTO> result = sopInitiationService.getAllSOPs();

        // Assert
        assertEquals(2, result.size());
        verify(sopRepository).findAll();
    }

    @Test
    void getSOPById_ExistingSOP_ReturnsSOPInitiation() throws SOPNotFoundException {
        // Arrange
        String sopId = "123";
        SOPInitiation sop = createSavedSOPInitiation();
        when(sopRepository.findById(sopId)).thenReturn(Optional.of(sop));

        // Act
        SOPInitiation result = sopInitiationService.getSOPById(sopId);

        // Assert
        assertNotNull(result);
        assertEquals(sop.getSopId(), result.getSopId());
    }

    @Test
    void getSOPById_NonExistentSOP_ThrowsException() {
        // Arrange
        String sopId = "nonexistent";
        when(sopRepository.findById(sopId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SOPNotFoundException.class, () -> sopInitiationService.getSOPById(sopId));
    }

    private SOPInitiationDTO createValidSOPInitiationDTO() {
        return SOPInitiationDTO.builder()
                .title("Test SOP")
                .visibility(Visibility.DEPARTMENT)
                .approvalPipeline(new ApprovalPipeline(1, Arrays.asList(3, 4),2))
                .build();
    }

    private SOPInitiation createSavedSOPInitiation() {
        return SOPInitiation.builder()
                .sopId("123")
                .title("Test SOP")
                .visibility(Visibility.DEPARTMENT)
                .approvalPipeline(new ApprovalPipeline(1, Arrays.asList(3, 4),2))
                .build();
    }
}