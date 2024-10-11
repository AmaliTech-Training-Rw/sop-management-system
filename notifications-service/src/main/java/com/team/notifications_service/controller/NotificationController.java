package com.team.notifications_service.controller;

import com.team.notifications_service.model.NotificationModel;
import com.team.notifications_service.repository.NotificationRepo;
import com.team.notifications_service.services.NotificationConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepo notificationRepository;

    @Autowired
    private NotificationConsumer notificationService;

    @GetMapping("/all")
    public List<NotificationModel> getAllNotifications() {
        return notificationRepository.findAll();
    }
    // Mark a notification as viewed
    @PutMapping("/{id}/viewed")
    public NotificationModel markAsViewed(@PathVariable String id) {
        return notificationService.markViewedNotification(id);
    }
}
