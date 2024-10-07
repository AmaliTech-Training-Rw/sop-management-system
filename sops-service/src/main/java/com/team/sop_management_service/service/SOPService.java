package com.team.sop_management_service.service;

import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.models.User;
import com.team.sop_management_service.repository.SOPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SOPService {

    @Autowired
    private SOPRepository sopRepository;

    public SOP createSOP(SOP sop) {
        // Save the SOP to the database
        return sopRepository.save(sop);
    }
    public List<SOP> getAllSOP() {
        return sopRepository.findAll();
    }


//    public List<User> getUsersByDepartment(String departmentId) {
//        // Fetch users from the same department based on the departmentId
//        // This method should be implemented based on your User repository
//    }
//
//    public List<User> getAllUsers() {
//        // Fetch all users from the User repository
//        // This method should be implemented based on your User repository
//    }
}
