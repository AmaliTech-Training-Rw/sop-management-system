package com.team.authentication_service.services;

import com.team.authentication_service.dtos.DepartmentDto;
import com.team.authentication_service.dtos.UserDto;
import com.team.authentication_service.models.User;
import com.team.authentication_service.models.UserRole;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.repositories.UserRoleRepository;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRoleRepository userRoleRepository;
    UserRepository userRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public UserDto getUserById(int id) throws Exception {
        Optional<User> possibleUser = userRepository.findById(id);
        List<UserRole> userRoles = userRoleRepository.findByUserId(id);


        if (possibleUser.isEmpty()) throw new ReturnableException("No user with given id", HttpStatus.NOT_FOUND);

        User user = possibleUser.get();

        System.out.println(user);
        System.out.println(userRoles);


        UserDto userDto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .position(user.getPosition().toString())
                .roles(
                        userRoles
                                .stream()
                                .map(
                                        userRole -> userRole
                                                .getRole()
                                                .getName()
                                )
                                .toList()
                )
                .department(
                        DepartmentDto
                                .builder()
                                .id(
                                        user.getDepartment().getId()
                                ).name(
                                        user.getDepartment().getName()
                                ).build()
                )
                .build();

        return userDto;
    }
}
