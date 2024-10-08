package com.team.authentication_service.services;

import com.team.authentication_service.dtos.CreateRoleRequestDto;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void add(CreateRoleRequestDto role) throws Exception {
        try {
            roleRepository.save(
                    Role.builder().role(role.getRoleName()).build()
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
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
