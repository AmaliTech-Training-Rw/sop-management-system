package com.team.messaging_service.repositories;

import com.team.messaging_service.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    // Find comments by user ID
    List<Comment> findByUserId(String userId);

    // Find comments related to a specific document by SOP document ID
    List<Comment> findBySopDocumentId(String sopDocumentId);

    // Find comments containing a specific keyword in the content
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    List<Comment> findByContentContaining(String keyword);

    // Custom query to count comments by user ID
    @Query(value = "{ 'userId': ?0 }", fields = "{ 'content' : 1 }")
    long countCommentsByUserId(String userId);
}
