package com.team.messaging_service.service;

import com.team.messaging_service.config.CommentProducer;
import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.model.Comment;
import com.team.messaging_service.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentProducer commentProducer;

    @InjectMocks
    private CommentService commentService;

    private CommentDTO commentDTO;
    private Comment comment;

    @BeforeEach
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Sample data
        commentDTO = new CommentDTO("user123", "This is a comment", "post456");
        comment = new Comment("1", "user123", "This is a comment", "post456", null);
    }

    @Test
    public void testCreateComment_Success() {
        // Arrange: Mocking repository save behavior
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act: Calling the service method
        Comment createdComment = commentService.createComment(commentDTO);

        // Assert: Verify the comment was saved and sent
        assertNotNull(createdComment);
        assertEquals(comment.getId(), createdComment.getId());
        assertEquals(comment.getContent(), createdComment.getContent());

        // Verify that the repository's save method was called
        verify(commentRepository, times(1)).save(any(Comment.class));

        // Verify that the producer's sendComment method was called
        verify(commentProducer, times(1)).sendComment(createdComment);
    }

    @Test
    public void testCreateComment_VerifyProducerCalledWithCorrectComment() {
        // Arrange: Mocking repository save behavior
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act: Call the service method
        commentService.createComment(commentDTO);

        // Assert: Capture the argument passed to the producer
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentProducer).sendComment(commentCaptor.capture());

        Comment sentComment = commentCaptor.getValue();
        assertEquals("This is a comment", sentComment.getContent());
        assertEquals("user123", sentComment.getUserId());
        assertEquals("post456", sentComment.getSopDocumentId());
    }

    @Test
    public void testGetAllComments_Success() {
        // Arrange: Mocking repository to return a list of comments
        List<Comment> comments = Arrays.asList(comment);
        when(commentRepository.findAll()).thenReturn(comments);

        // Act: Calling the service method
        List<Comment> returnedComments = commentService.getAllComments();

        // Assert: Verify the size and content
        assertEquals(1, returnedComments.size());
        assertEquals("This is a comment", returnedComments.get(0).getContent());

        // Verify that the repository's findAll method was called
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    public void testGetCommentById_Success() {
        // Arrange: Mocking repository to return an optional comment
        when(commentRepository.findById("1")).thenReturn(Optional.of(comment));

        // Act: Calling the service method
        Comment foundComment = commentService.getCommentById("1");

        // Assert: Verify the comment is returned correctly
        assertNotNull(foundComment);
        assertEquals("This is a comment", foundComment.getContent());

        // Verify that the repository's findById method was called
        verify(commentRepository, times(1)).findById("1");
    }

    @Test
    public void testGetCommentById_NotFound() {
        // Arrange: Mocking repository to return an empty optional
        when(commentRepository.findById("1")).thenReturn(Optional.empty());

        // Act: Calling the service method
        Comment foundComment = commentService.getCommentById("1");

        // Assert: Verify that null is returned when the comment is not found
        assertNull(foundComment);

        // Verify that the repository's findById method was called
        verify(commentRepository, times(1)).findById("1");
    }
}
