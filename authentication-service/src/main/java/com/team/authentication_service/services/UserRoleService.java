package com.team.authentication_service.services;

import com.team.authentication_service.dtos.AssignRoleReqDto;
import com.team.authentication_service.models.Role;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.repositories.RoleRepository;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.repositories.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRoleService {
    RoleRepository roleRepository;
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;

    public UserRoleService(RoleRepository roleRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public void assignRole(AssignRoleReqDto assignRoleReqDto) throws Exception {
        Optional<User> user = userRepository.findById(assignRoleReqDto.getUserId());
        Optional<Role> role = roleRepository.findById(assignRoleReqDto.getRoleId());

        if (user.isEmpty()) throw new Exception("Couldn't find user of id " + assignRoleReqDto.getUserId());

        if (role.isEmpty()) throw new Exception("Couldn't find role of id " + assignRoleReqDto.getRoleId());

        try {
            userRoleRepository.save(
                    UserRole.builder()
                            .user(user.get())
                            .role(role.get())
                            .build()
            );
        } catch (Exception e) {
            throw new Exception("Couldn't assign role " + assignRoleReqDto.getRoleId());
        }
    }

    public List<UserRole> getAllUserRoles(int userId) throws Exception {
        List<UserRole> roles = userRoleRepository.findByUserId(userId);
        try {
            return roles;
        } catch (Exception e) {
            throw new Exception("Couldn't get all user roles");
        }
    }

    public void removeRole(User user, Role role) {

    }
}
