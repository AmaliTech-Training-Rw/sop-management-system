package com.team.authentication_service.services;

import com.team.authentication_service.dtos.UserDto;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public UserDto getUserById(int id) throws Exception {
//        try {
//            Optional<User> possibleUser = userRepository.findById(id);
//
//            if (!possibleUser.isPresent()) {
//                throw new Exception("No user with given id");
//            }
//
//            User user = possibleUser.get();
//
//            UserDto userDto = UserDto.builder()
//                    .name(user.getName())
//                    .email(user.getEmail())
//                    .position(user.getPosition())
//                    .roles(user.getRoles())
//                    .department(user.getDepartmentId())
//                    .build();
//
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }
}
