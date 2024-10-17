package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.AssignRoleReqDto;
import com.team.authentication_service.dtos.ResponseDto;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.services.UserRoleService;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@RestController("/user-roles")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(final UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/assign")
    public ResponseEntity<Object> assignRole(@Valid @RequestBody AssignRoleReqDto assignRoleReqDto) throws Exception {
        try {
//          Assign role to user
            userRoleService.assignRole(assignRoleReqDto);
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Role assigned")
                            .data(null)
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ResponseDto.builder().message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/roles/{userId}")
    public ResponseEntity<Object> getRoles(@PathVariable int userId) throws Exception {
        try {
//          Get roles of a user
            List<UserRole> roles = userRoleService.getAllUserRoles(userId);

            return ResponseEntity.ok(roles);
        } catch (ReturnableException e) {
            return ResponseEntity.status(e.getStatusCode()).body(
                    ResponseDto.builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
}
