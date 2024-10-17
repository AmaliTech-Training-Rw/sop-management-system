package com.team.authentication_service.services;

import com.team.authentication_service.dtos.CreateRoleRequestDto;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.RoleRepository;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {
    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void add(CreateRoleRequestDto role) throws ReturnableException {
        Optional<Role> newRole = roleRepository.findByName(role.getRoleName());

        if (newRole.isPresent()) throw new ReturnableException("Role already exists", HttpStatus.BAD_REQUEST);

        roleRepository.save(
                Role.builder().name(role.getRoleName()).build()
        );
    }

    public List<Role> getRoles() throws Exception {
        try {
            List<Role> roles = roleRepository.findAll();
            return roles;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Role getRole(int id) throws Exception {
        try {
            Optional<Role> role = roleRepository.findById(id);

            if (role.isEmpty()) throw new Exception("Can't find role with id: " + id);

            return role.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
