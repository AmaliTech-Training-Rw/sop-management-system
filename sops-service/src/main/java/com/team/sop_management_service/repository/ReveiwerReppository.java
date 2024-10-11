package com.team.sop_management_service.repository;

import com.team.sop_management_service.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReveiwerReppository extends MongoRepository<Review, String> {
}
