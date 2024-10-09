package com.team.messaging_service.controller;

import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.model.Comment;
import com.team.messaging_service.service.CommentService;
import com.team.messaging_service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDTO commentDTO) {
        try {
            Comment createdComment = commentService.createComment(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (CommentExceptions.InvalidCommentDataException e) {
            throw new CommentExceptions.CommentCreationException("Failed to create comment: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            throw new CommentExceptions.CommentNotFoundException("Comment not found with id: " + id);
        }
        return ResponseEntity.ok(comment);
    }
}