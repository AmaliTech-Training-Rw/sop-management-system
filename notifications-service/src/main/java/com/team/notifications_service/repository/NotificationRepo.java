package com.team.notifications_service.repository;

import com.team.notifications_service.model.NotificationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepo extends MongoRepository<NotificationModel, String> {
}
