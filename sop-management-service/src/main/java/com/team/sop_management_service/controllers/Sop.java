package com.team.sop_management_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sop-management")
public class Sop {
    @GetMapping("/example")
    public String example() {
        return "Hello World";
    }
}
