//package com.team.sop_management_service;
//
//import com.team.sop_management_service.error.InvalidSOPException;
//import com.team.sop_management_service.models.SOPInitiation;
//import com.team.sop_management_service.models.ApprovalPipeline;
//import com.team.sop_management_service.models.User;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.enums.Visibility;
//import com.team.sop_management_service.service.SOPInitiationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.slf4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class SOPServiceTest {
//
//    @Mock
//    private SOPInitiationRepository sopRepository;
//
//    @Mock
//    private Logger logger;
//
//    @InjectMocks
//    private SOPInitiationService sopService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testInitiateSOP_ValidSOP_WithMultipleReviewers_ShouldSaveAndReturnSOP() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setTitle("Test SOP with Multiple Reviewers");
//        sop.setVisibility(Visibility.DEPARTMENT);
//
//        // Create an approval pipeline with HoD and multiple reviewers
//        ApprovalPipeline pipeline = new ApprovalPipeline();
//        User hod = new User();
//        hod.setDepartment("IT");
//        pipeline.setApprover(hod);
//
//        User author = new User();
//        author.setDepartment("IT");
//        pipeline.setAuthor(author);
//
//        List<User> reviewers = new ArrayList<>();
//        User reviewer1 = new User();
//        reviewer1.setDepartment("IT");
//        User reviewer2 = new User();
//        reviewer2.setDepartment("IT");
//        reviewers.add(reviewer1);
//        reviewers.add(reviewer2);
//        pipeline.setReviewers(reviewers);
//
//        sop.setApprovalPipeline(pipeline);
//        when(sopRepository.save(any(SOPInitiation.class))).thenReturn(sop);
//
//        // Act
//        SOPInitiation result = sopService.initiateSOP(sop);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Test SOP with Multiple Reviewers", result.getTitle());
//        verify(sopRepository, times(1)).save(sop);
//    }
//
//    @Test
//    void testInitiateSOP_InvalidSOP_ReviewersNotFromSameDepartment_ShouldThrowInvalidSOPException() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setTitle("Invalid SOP with Incorrect Reviewers");
//        sop.setVisibility(Visibility.DEPARTMENT);
//
//        // Create an approval pipeline with HoD and reviewers from different departments
//        ApprovalPipeline pipeline = new ApprovalPipeline();
//        User hod = new User();
//        hod.setDepartment("IT");
//        pipeline.setApprover(hod);
//
//        User author = new User();
//        author.setDepartment("IT");
//        pipeline.setAuthor(author);
//
//        List<User> reviewers = new ArrayList<>();
//        User reviewer1 = new User();
//        reviewer1.setDepartment("IT");
//        User reviewer2 = new User();
//        reviewer2.setDepartment("HR"); // Reviewer from a different department
//        reviewers.add(reviewer1);
//        reviewers.add(reviewer2);
//        pipeline.setReviewers(reviewers);
//
//        sop.setApprovalPipeline(pipeline);
//
//        // Act & Assert
//        assertThrows(InvalidSOPException.class, () -> sopService.initiateSOP(sop));
//    }
//
////    @Test
////    void testInitiateMultipleSOPs_ShouldSaveAll() {
////        // Arrange
////        SOP sop1 = new SOP();
////        sop1.setTitle("Test SOP 1");
////        sop1.setVisibility(Visibility.DEPARTMENT);
////
////        SOP sop2 = new SOP();
////        sop2.setTitle("Test SOP 2");
////        sop2.setVisibility(Visibility.ORGANIZATION);
////
////        SOP sop3 = new SOP();
////        sop3.setTitle("Test SOP 3");
////        sop3.setVisibility(Visibility.DEPARTMENT);
////
////        when(sopRepository.save(any(SOP.class))).thenAnswer(invocation -> invocation.getArgument(0));
////
////        // Act
////        SOP result1 = sopService.initiateSOP(sop1);
//////        SOP result2 = sopService.initiateSOP(sop2);
//////        SOP result3 = sopService.initiateSOP(sop3);
////
////        // Assert
////        assertNotNull(result1);
////        assertEquals("Test SOP 1", result1.getTitle());
////
//////        assertNotNull(result2);
//////        assertEquals("Test SOP 2", result2.getTitle());
//////
//////        assertNotNull(result3);
//////        assertEquals("Test SOP 3", result3.getTitle());
////
////        verify(sopRepository, times(1)).save(any(SOP.class));
////    }
//
//    @Test
//    void testDeleteSOP_ValidId_ShouldDeleteSOP() {
//        // Act
//        sopService.deleteSOP("123");
//
//        // Assert
//        verify(sopRepository, times(1)).deleteById("123");
//    }
//
//    @Test
//    void testGetSOPById_SOPExists_ShouldReturnSOP() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setSopId("123");
//        when(sopRepository.findById("123")).thenReturn(Optional.of(sop));
//
//        // Act
//        SOPInitiation result = sopService.getSOPById("123");
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("123", result.getSopId());
//        verify(sopRepository, times(1)).findById("123");
//    }
//
//    @Test
//    void testGetSOPById_SOPDoesNotExist_ShouldThrowException() {
//        // Arrange
//        when(sopRepository.findById("123")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> sopService.getSOPById("123"));
//    }
//}
