package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.CreateRoleRequestDto;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {
    RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<String> createRole(@Valid @RequestBody CreateRoleRequestDto role) {
        try {
            roleService.add(role);
            return ResponseEntity.ok("Saved");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllRoles() {
        try {
            return ResponseEntity.ok(roleService.getRoles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> getRole(@PathVariable int id) {
        try {
            return ResponseEntity.ok(roleService.getRole(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
