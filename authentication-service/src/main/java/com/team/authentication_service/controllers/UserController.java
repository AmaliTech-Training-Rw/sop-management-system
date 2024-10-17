package com.team.authentication_service.controllers;

import com.team.authentication_service.dtos.ResponseDto;
import com.team.authentication_service.dtos.UserDto;
import com.team.authentication_service.services.UserService;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
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
    public ResponseEntity<Object> getUserInfo(@PathVariable int id) {
        try {
//          Get user information by id
            UserDto userDto = userService.getUserById(id);

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .message("Success")
                            .data(userDto)
                            .build()
            );
        } catch (ReturnableException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseDto.builder()
                                    .message("Internal server error. Contact tech support.")
                                    .data(null)
                                    .build()
                    );
        }
    }
}