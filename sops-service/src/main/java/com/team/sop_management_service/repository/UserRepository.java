package com.team.sop_management_service.repository;

import com.team.sop_management_service.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Custom query methods can be added here if necessary
}
