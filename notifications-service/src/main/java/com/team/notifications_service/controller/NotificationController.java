package com.team.notifications_service.controller;

import com.team.notifications_service.model.NotificationModel;
import com.team.notifications_service.repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepo notificationRepository;

    @GetMapping("/all")
    public List<NotificationModel> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
