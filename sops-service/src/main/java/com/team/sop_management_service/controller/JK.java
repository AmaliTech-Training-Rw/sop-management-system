//package com.team.sop_management_service.controller;
//
//import com.team.sop_management_service.dto.SOPCreationDTO;
//import com.team.sop_management_service.service.SOPService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/sop")
//@RequiredArgsConstructor
//@Tag(name = "SOP Management", description = "API for managing Standard Operating Procedures")
//public class SOPCreationVersionController {
//
//    private final SOPService sopService;
//
//    @PostMapping
//    @Operation(summary = "Create a new SOP", description = "Creates a new Standard Operating Procedure")
//    @ApiResponse(responseCode = "201", description = "SOP created successfully")
//    public ResponseEntity<SOPCreationDTO> createSOP(@Valid @RequestBody SOPCreationDTO SOPCreationDTO) {
//        SOPCreationDTO createdSOP = sopService.createSOP(SOPCreationDTO);
//        return new ResponseEntity<>(createdSOP, HttpStatus.CREATED);
//    }
//
//
//}
