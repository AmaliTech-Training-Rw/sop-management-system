package com.team.sop_management_service.controller;

import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.service.SOPService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sops")
public class SOPController {

    @Autowired
    private SOPService sopService;

    @PostMapping
    public ResponseEntity<SOP> createSOP(@Valid @RequestBody SOP sop) {
        SOP createdSOP = sopService.createSOP(sop);
        // Send notification to the Author
        // sendEmailNotification(createdSOP.getAuthor().getEmail());
        return ResponseEntity.ok(createdSOP);
    }

    @GetMapping("/sops")
    public ResponseEntity<List<SOP>> getUsersByDepartment() {
        List<SOP> users = sopService.getAllSOP();
        return ResponseEntity.ok(users);
    }
//
//    @GetMapping("/users/all")
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = sopService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
}
