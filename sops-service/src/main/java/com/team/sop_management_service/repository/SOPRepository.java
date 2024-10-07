package com.team.sop_management_service.repository;

import com.team.sop_management_service.models.SOP;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SOPRepository extends MongoRepository<SOP, String> {
    // You can define custom query methods here if needed
}
