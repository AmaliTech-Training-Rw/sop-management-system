package com.team.authentication_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.authentication_service.dtos.AuthenticatedUserHeaders;
import com.team.authentication_service.dtos.CreateRoleRequestDto;
import com.team.authentication_service.dtos.ResponseDto;
import com.team.authentication_service.services.RoleService;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {
    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping({"", "/add"})
    public ResponseEntity<Object> createRole(
            @Valid @RequestBody CreateRoleRequestDto role,
            @RequestHeader("x-auth-user-email") String adminHeaderJson
    ) {
        try {
//          Extract user headers from the user
            ObjectMapper objectMapper = new ObjectMapper();
            AuthenticatedUserHeaders adminHeaders = objectMapper.readValue(adminHeaderJson, AuthenticatedUserHeaders.class);


//          Check whether the user has the necessary rights
            if (!adminHeaders.getPosition().equals("SUPER_ADMIN"))
                throw new ReturnableException("You're no the super admin. Contact the admin to get registered.", HttpStatus.FORBIDDEN);

//           Add role
            roleService.add(role);

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success adding role")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage()).build()
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support")
                                    .build()
                    );
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllRoles(
            @RequestHeader("x-auth-user-email") String adminHeaderJson
    ) {
//        get and return all user roles
        try {
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(roleService.getRoles())
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support")
                                    .build()
                    );
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> getRole(
            @PathVariable int id,
            @RequestHeader("x-auth-user-email") String adminHeaderJson
    ) {
        try {
//          get and return user roles
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(roleService.getRole(id)).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support")
                                    .build()
                    );
        }
    }
}
