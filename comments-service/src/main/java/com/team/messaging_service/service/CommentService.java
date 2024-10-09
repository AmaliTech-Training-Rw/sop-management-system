package com.team.messaging_service.service;

import com.team.messaging_service.config.CommentProducer;
import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.model.Comment;
import com.team.messaging_service.repositories.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentProducer commentProducer;

    public CommentService(CommentRepository commentRepository, CommentProducer commentProducer) {
        this.commentRepository = commentRepository;
        this.commentProducer = commentProducer;
    }

    public Comment createComment(CommentDTO commentDTO) {
        Comment comment = new Comment(commentDTO.getContent(), commentDTO.getUserId(), commentDTO.getSopDocumentId());
        Comment savedComment = commentRepository.save(comment);
        commentProducer.sendComment(savedComment);
        return savedComment;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(String id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null); // Return null if not found
    }






}
