package com.team.notifications_service.config;

import com.team.notifications_service.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "authentication-service")
public interface UserFeignClient {
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable int id);
}
