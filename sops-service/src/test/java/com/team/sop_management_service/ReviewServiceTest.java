package com.team.sop_management_service;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.models.*;
import com.team.sop_management_service.repository.ReveiwerReppository;
import com.team.sop_management_service.repository.SOPCreationRepository;
import com.team.sop_management_service.repository.SOPInitiationRepository;
import com.team.sop_management_service.repository.UserRepository;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.config.NotificationService;
import com.team.sop_management_service.service.ReviewService;
import com.team.sop_management_service.service.SOPCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private SOPCreationRepository sopRepository;

    @Mock
    private SOPInitiationRepository sopInitiationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SOPCreationService sopCreationService;

    @InjectMocks
    private ReviewService reviewService;

    private SOPCreation sopCreation;
    private SOPInitiation sopInitiation;
    private User reviewer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up test SOPCreation and SOPInitiation
        sopCreation = new SOPCreation();
        sopCreation.setId("sop1");
        sopCreation.setStatus(SOPStatus.SUBMITTED);

        sopInitiation = new SOPInitiation();
        sopInitiation.setSopId("sopInit1");

        ApprovalPipeline approvalPipeline = new ApprovalPipeline();
        reviewer = new User();
        reviewer.setId("reviewer1");
        approvalPipeline.setReviewers(List.of(reviewer));

        sopInitiation.setApprovalPipeline(approvalPipeline);
        sopCreation.setSopInitiation(sopInitiation);
    }

    @Test
    void reviewSOP_ShouldThrowException_WhenSOPNotFound() {
        when(sopRepository.findById("sop1")).thenReturn(Optional.empty());

        assertThrows(SOPNotFoundException.class, () -> {
            reviewService.reviewSOP("sop1", "reviewer1", true);
        });
    }

    @Test
    void reviewSOP_ShouldThrowException_WhenSOPNotSubmitted() {
        sopCreation.setStatus(SOPStatus.DRAFT);
        when(sopRepository.findById("sop1")).thenReturn(Optional.of(sopCreation));

        assertThrows(IllegalStateException.class, () -> {
            reviewService.reviewSOP("sop1", "reviewer1", true);
        });
    }

    @Test
    void reviewSOP_ShouldThrowException_WhenReviewerNotFound() {
        when(sopRepository.findById("sop1")).thenReturn(Optional.of(sopCreation));
        when(sopInitiationRepository.findById("sopInit1")).thenReturn(Optional.of(sopInitiation));
        when(userRepository.findById("reviewer1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            reviewService.reviewSOP("sop1", "reviewer1", true);
        });
    }

    @Test
    void reviewSOP_ShouldThrowException_WhenReviewerNotInApprovalPipeline() {
        User invalidReviewer = new User();
        invalidReviewer.setId("reviewer2");

        when(sopRepository.findById("sop1")).thenReturn(Optional.of(sopCreation));
        when(sopInitiationRepository.findById("sopInit1")).thenReturn(Optional.of(sopInitiation));
        when(userRepository.findById("reviewer2")).thenReturn(Optional.of(invalidReviewer));

        assertThrows(RuntimeException.class, () -> {
            reviewService.reviewSOP("sop1", "reviewer2", true);
        });
    }

    @Test
    void reviewSOP_ShouldUpdateSOPStatus_WhenAllReviewersConfirm() {
        when(sopRepository.findById("sop1")).thenReturn(Optional.of(sopCreation));
        when(sopInitiationRepository.findById("sopInit1")).thenReturn(Optional.of(sopInitiation));
        when(userRepository.findById("reviewer1")).thenReturn(Optional.of(reviewer));

        reviewService.reviewSOP("sop1", "reviewer1", true);

        verify(sopCreationService, times(1)).updateSOPStatus("sop1", SOPStatus.REVIEW);
    }
}
