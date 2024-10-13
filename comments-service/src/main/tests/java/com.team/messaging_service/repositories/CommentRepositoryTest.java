package com.team.messaging_service.repositories;

import com.team.messaging_service.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    public void setup() {
        // Clear existing data in the repository
        commentRepository.deleteAll();

        // Sample comments for testing
        comment1 = new Comment("1", "user123", "This is a comment from user123", "sopDoc001", null);
        comment2 = new Comment("2", "user456", "Another comment from user456", "sopDoc002", null);
        comment3 = new Comment("3", "user123", "A second comment from user123", "sopDoc001", null);

        // Save the sample comments
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
    }

    @Test
    public void testFindByUserId() {
        // Act: Fetch comments by userId
        List<Comment> comments = commentRepository.findByUserId("user123");

        // Assert: Verify that two comments are returned
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("user123", comments.get(0).getUserId());
    }

    @Test
    public void testFindBySopDocumentId() {
        // Act: Fetch comments by sopDocumentId
        List<Comment> comments = commentRepository.findBySopDocumentId("sopDoc001");

        // Assert: Verify that two comments are returned for sopDoc001
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("sopDoc001", comments.get(0).getSopDocumentId());
    }

    @Test
    public void testFindByContentContaining() {
        // Act: Fetch comments containing the keyword "Another"
        List<Comment> comments = commentRepository.findByContentContaining("Another");

        // Assert: Verify that one comment is returned
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertTrue(comments.get(0).getContent().contains("Another"));
    }

    @Test
    public void testCountCommentsByUserId() {
        // Act: Count the number of comments by userId
        long count = commentRepository.countCommentsByUserId("user123");

        // Assert: Verify that the count is 2
        assertEquals(2, count);
    }

    @Test
    public void testCountCommentsByUserId_NoComments() {
        // Act: Count comments for a non-existing userId
        long count = commentRepository.countCommentsByUserId("nonExistingUser");

        // Assert: Verify that the count is 0
        assertEquals(0, count);
    }
}
