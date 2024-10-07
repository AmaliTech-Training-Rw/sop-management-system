package com.team.sop_management_service;

import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.repository.SOPRepository;
import com.team.sop_management_service.service.SOPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SOPServiceTest {

    @Mock
    private SOPRepository sopRepository;

    @InjectMocks
    private SOPService sopService;

    private SOP mockSOP;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SOP object for testing
        mockSOP = new SOP();
        mockSOP.setSopId("sop123");
        mockSOP.setTitle("Test SOP");
        // Add other fields as needed
    }

    @Test
    void testCreateSOP() {
        // Arrange: Mock the repository to return the mock SOP when saving
        when(sopRepository.save(mockSOP)).thenReturn(mockSOP);

        // Act: Call the createSOP method
        SOP savedSOP = sopService.createSOP(mockSOP);

        // Assert: Verify that the save method was called and the returned SOP is the same
        verify(sopRepository, times(1)).save(mockSOP);
        assertEquals(mockSOP, savedSOP);
    }
}
