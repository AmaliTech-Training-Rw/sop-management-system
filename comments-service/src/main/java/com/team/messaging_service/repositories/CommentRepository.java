package com.team.messaging_service.repositories;

import com.team.messaging_service.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    // Find comments related to a specific SOP document by SOP document ID
    List<Comment> findBySopDocumentId(String sopDocumentId);

    // Find comments containing a specific keyword in the content
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    List<Comment> findByContentContaining(String keyword);

    // Find the latest comments by createdAt field (sort by descending order)
    List<Comment> findAllByOrderByCreatedAtDesc();
}
