package com.team.sop_management_service.authenticationService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "authentication-service")
public interface AuthenticationServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userId") int userId);
}
