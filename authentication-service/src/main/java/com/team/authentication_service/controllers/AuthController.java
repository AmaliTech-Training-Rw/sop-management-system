package com.team.authentication_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class AuthController {
    @GetMapping("/login")
    public String login() {
        return "Login user";
    }
}
