//package com.team.sop_management_service;
//
//import com.team.sop_management_service.models.SOPInitiation;
//import com.team.sop_management_service.models.ApprovalPipeline;
//import com.team.sop_management_service.models.User;
//import com.team.sop_management_service.repository.SOPInitiationRepository;
//import com.team.sop_management_service.enums.Visibility;
//import com.team.sop_management_service.service.SOPInitiationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureDataMongo
////@ActiveProfiles("test") // Assuming you have a 'test' profile for MongoDB Atlas connection
//class SOPServiceIntegrationTest {
//
//    @Autowired
//    private SOPInitiationService sopService;
//
//    @Autowired
//    private SOPInitiationRepository sopRepository;
//
//    @BeforeEach
//    void setUp() {
//        sopRepository.deleteAll(); // Clean up before each test
//    }
//
//    @Test
//    void testSaveSOPWithMultipleReviews() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setTitle("Test SOP with Reviews");
//        sop.setVisibility(Visibility.DEPARTMENT);
//
//        // Set up the Approval Pipeline with HOD
//        ApprovalPipeline pipeline = new ApprovalPipeline();
//        User hod = new User();
//        hod.setDepartment("IT");
//        pipeline.setApprover(hod);
//
//        // Set up reviewers from the same department
//        List<User> reviewers = new ArrayList<>();
//        User reviewer1 = new User();
//        reviewer1.setId("1");
//        reviewer1.setDepartment("IT");
//        reviewers.add(reviewer1);
//
//        User reviewer2 = new User();
//        reviewer2.setId("2");
//        reviewer2.setDepartment("IT");
//        reviewers.add(reviewer2);
//
//        pipeline.setReviewers(reviewers); // Assuming ApprovalPipeline has a setReviewers method
//        sop.setApprovalPipeline(pipeline);
//
//        // Act
//        SOPInitiation savedSOP = sopService.saveSOP(sop);
//
//        // Assert
//        assertNotNull(savedSOP);
//        assertEquals("Test SOP with Reviews", savedSOP.getTitle());
//
//        // Verify in the database
//        SOPInitiation fetchedSOP = sopRepository.findById(savedSOP.getSopId()).orElse(null);
//        assertNotNull(fetchedSOP);
//        assertEquals("Test SOP with Reviews", fetchedSOP.getTitle());
//        assertEquals(2, fetchedSOP.getApprovalPipeline().getReviewers().size()); // Check number of reviewers
//    }
//
//    @Test
//    void testSaveSOPWithDifferentDepartmentReviewers() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setTitle("Test SOP with Different Department Reviewers");
//        sop.setVisibility(Visibility.DEPARTMENT);
//
//        // Set up the Approval Pipeline with HOD
//        ApprovalPipeline pipeline = new ApprovalPipeline();
//        User hod = new User();
//        hod.setDepartment("IT");
//        pipeline.setApprover(hod);
//
//        // Set up reviewers from a different department
//        List<User> reviewers = new ArrayList<>();
//        User reviewer1 = new User();
//        reviewer1.setDepartment("HR");
//        reviewers.add(reviewer1);
//
//        User reviewer2 = new User();
//        reviewer2.setDepartment("Finance");
//        reviewers.add(reviewer2);
//
//        pipeline.setReviewers(reviewers);
//        sop.setApprovalPipeline(pipeline);
//
//        // Act and Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            sopService.saveSOP(sop); // Assuming your service throws an exception if reviewers are from different departments
//        });
//
//        String expectedMessage = "Reviewers must be from the same department as the HOD";
//        assertEquals(expectedMessage, exception.getMessage());
//    }
//
//    @Test
//    void testGetAllSOPs() {
//        // Arrange
//        SOPInitiation sop1 = new SOPInitiation();
//        sop1.setTitle("SOP 1");
//        sop1.setVisibility(Visibility.ORGANIZATION);
//        sopRepository.save(sop1);
//
//        SOPInitiation sop2 = new SOPInitiation();
//        sop2.setTitle("SOP 2");
//        sop2.setVisibility(Visibility.DEPARTMENT);
//        sopRepository.save(sop2);
//
//        // Act
//        List<SOPInitiation> allSOPs = sopService.getAllSOPs();
//
//        // Assert
//        assertEquals(2, allSOPs.size());
//    }
//
//    @Test
//    void testDeleteSOP() {
//        // Arrange
//        SOPInitiation sop = new SOPInitiation();
//        sop.setTitle("SOP to Delete");
//        SOPInitiation savedSOP = sopRepository.save(sop);
//
//        // Act
//        sopService.deleteSOP(savedSOP.getSopId());
//
//        // Assert
//        assertFalse(sopRepository.findById(savedSOP.getSopId()).isPresent());
//    }
//
//    // Add additional tests as needed
//}
