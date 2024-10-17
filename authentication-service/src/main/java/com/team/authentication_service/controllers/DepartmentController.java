package com.team.authentication_service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.authentication_service.dtos.AssigneHodReqDto;
import com.team.authentication_service.dtos.AuthenticatedUserHeaders;
import com.team.authentication_service.dtos.CreateDepartmentRequestDto;
import com.team.authentication_service.dtos.ResponseDto;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.services.DepartmentService;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
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

    @PostMapping({"/", ""})
    public ResponseEntity<Object> addDepartment(
            @Valid @RequestBody CreateDepartmentRequestDto departmentReqDto,
            @RequestHeader("x-auth-user-email") String adminHeaderJson
    ) {
        try {
//          Get the user info in the headers
            ObjectMapper objectMapper = new ObjectMapper();
            AuthenticatedUserHeaders adminHeaders = objectMapper
                    .readValue(adminHeaderJson, AuthenticatedUserHeaders.class);

//          Check whether the user has the necessary rights to add a new department
            if (!adminHeaders.getPosition().equals("SUPER_ADMIN"))
                throw new ReturnableException(
                        "You're no the super admin. Contact the admin to get registered.",
                        HttpStatus.FORBIDDEN
                );

//          Add new department
            departmentService.addDepartment(departmentReqDto);

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success adding department")
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }

    @GetMapping({"/", ""})
    public ResponseEntity<Object> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(departments)
                            .build()
            );
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Object> getDepartment(@PathVariable int departmentId) {
        try {
//          Get department
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(departmentService.getDepartmentById(departmentId))
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support.")
                                    .data(null)
                    );
        }
    }

    @PostMapping("/assign-hod")
    public ResponseEntity<Object> assignHod(
            @Valid @RequestBody AssigneHodReqDto assignHodReqDto,
            @RequestHeader("x-auth-user-email") String adminHeaderJson
    ) {
        try {
//          Parse user info from headers
            ObjectMapper objectMapper = new ObjectMapper();
            AuthenticatedUserHeaders adminHeaders = objectMapper
                    .readValue(adminHeaderJson, AuthenticatedUserHeaders.class);

//          Check whether current user has the necessary rights
            if (!adminHeaders.getPosition().equals("SUPER_ADMIN"))
                throw new ReturnableException(
                        "You're no the super admin. Contact the admin to get registered.",
                        HttpStatus.FORBIDDEN
                );

//          Assign HOD to department
            departmentService.assignHod(assignHodReqDto);

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Assigned hod successfully")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Error adding department. Please contact tech support")
                                    .data(null)
                                    .build()
                    );
        }
    }
}
