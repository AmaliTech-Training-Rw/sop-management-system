package com.team.notifications_service.controllers;

import com.team.notifications_service.config.UserFeignClient;
import com.team.notifications_service.dtos.AuthenticatedUserHeaders;
import com.team.notifications_service.dtos.UserDto;
import com.team.notifications_service.kafkaServices.KafkaProducer;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Example {
    KafkaProducer kafkaProducer;
    private UserFeignClient userFeignClient;

    public Example(KafkaProducer kafkaProducer, UserFeignClient userFeignClient) {
        this.kafkaProducer = kafkaProducer;
        this.userFeignClient = userFeignClient;
    }


    @GetMapping("/test/{id}")
    public String hello(@PathVariable int id, @RequestHeader("x-auth-user-email") AuthenticatedUserHeaders email) {
        try {
//            ResponseEntity<UserDto> res = userFeignClient.getUser(id);
//            System.out.println("############## getting user dto  ##########");
//            System.out.println(res.getBody());
//            System.out.println("############## getting user dto  ##########");
//            kafkaProducer.sendMessage(
//                    "user-details-req",
//                    id
//            );

            System.out.println("*******************************");
            System.out.println("Email already in the controller");
            System.out.println(email);
            System.out.println("*******************************");
            return "Check console";
        } catch (FeignException e) {
            return e.getMessage();
        }
    }
}


