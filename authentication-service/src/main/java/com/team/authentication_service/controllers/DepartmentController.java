package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.AssigneHodReqDto;
import com.team.authentication_service.dtos.CreateDepartmentRequestDto;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.services.DepartmentService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDepartment(@Valid @RequestBody CreateDepartmentRequestDto departmentReqDto) {
        try {
            departmentService.addDepartment(departmentReqDto);
            return ResponseEntity.ok("Department added successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity("An issue occured. Contact tech support.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Object> getDepartment(@PathVariable int departmentId) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentById(departmentId));
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/assign-hod")
    public ResponseEntity<Object> assignHod(@Valid AssigneHodReqDto assigneHodReqDto) {
        try {
            departmentService.assigneHod(assigneHodReqDto);
            return ResponseEntity.ok("Assigned hod successfully");
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
