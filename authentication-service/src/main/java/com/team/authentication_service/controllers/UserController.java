package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.UserDto;
import com.team.authentication_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {
        try {
            UserDto userDto = userService.getUserById(id);
            System.out.println("############## getting user dto  ##########");
            System.out.println(userDto);
            System.out.println("############## getting user dto  ##########");
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}