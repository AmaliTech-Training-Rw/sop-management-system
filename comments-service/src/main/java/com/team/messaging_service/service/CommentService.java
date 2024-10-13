package com.team.messaging_service.service;

import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.dto.CommentResponseDTO;
import com.team.messaging_service.dto.PagedResponse;
import com.team.messaging_service.exceptions.CommentExceptions;
import com.team.messaging_service.model.Comment;
import com.team.messaging_service.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    // Create new comment
    public CommentResponseDTO createComment(CommentDTO commentDTO) {
        try {
            Comment comment = commentMapper.toEntity(commentDTO);
            Comment savedComment = commentRepository.save(comment);
            return commentMapper.toResponseDTO(savedComment);
        } catch (IllegalArgumentException e) {
            throw new CommentExceptions.InvalidCommentDataException("Invalid comment data provided: " + e.getMessage());
        } catch (Exception e) {
            throw new CommentExceptions.CommentCreationException("Failed to create comment: " + e.getMessage());
        }
    }

    // Fetch paginated comments
    public PagedResponse<CommentResponseDTO> getAllComments(int page, int size) {
        try {
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Invalid pagination parameters");
            }

            Page<Comment> commentPage = commentRepository.findAll(PageRequest.of(page, size));
            List<CommentResponseDTO> comments = commentPage.stream()
                    .map(commentMapper::toResponseDTO)
                    .collect(Collectors.toList());

            return new PagedResponse<>(
                    comments,
                    page,
                    size,
                    commentPage.getTotalElements(),
                    commentPage.getTotalPages()
            );
        } catch (IllegalArgumentException e) {
            throw new CommentExceptions.InvalidPaginationParametersException("Invalid pagination parameters: " + e.getMessage());
        } catch (Exception e) {
            throw new CommentExceptions.CommentFetchException("Failed to fetch comments: " + e.getMessage());
        }
    }

    // Fetch latest comments
    public List<CommentResponseDTO> getLatestComments() {
        try {
            List<Comment> comments = commentRepository.findAllByOrderByCreatedAtDesc();
            return comments.stream()
                    .map(commentMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CommentExceptions.CommentFetchException("Failed to fetch latest comments: " + e.getMessage());
        }
    }

    // Fetch a comment by ID
    public CommentResponseDTO getCommentById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Comment ID cannot be null or empty");
            }

            return commentRepository.findById(id)
                    .map(commentMapper::toResponseDTO)
                    .orElseThrow(() -> new CommentExceptions.CommentNotFoundException(id));
        } catch (IllegalArgumentException e) {
            throw new CommentExceptions.InvalidCommentIdException("Invalid comment ID: " + e.getMessage());
        } catch (CommentExceptions.CommentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CommentExceptions.CommentFetchException("Failed to fetch comment: " + e.getMessage());
        }
    }

    // Find comments by SOP Document ID
    public List<CommentResponseDTO> getCommentsBySopDocumentId(String sopDocumentId) {
        try {
            if (sopDocumentId == null || sopDocumentId.trim().isEmpty()) {
                throw new IllegalArgumentException("SOP Document ID cannot be null or empty");
            }

            List<Comment> comments = commentRepository.findBySopDocumentId(sopDocumentId);
            return comments.stream()
                    .map(commentMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CommentExceptions.InvalidSopDocumentIdException("Invalid SOP Document ID: " + e.getMessage());
        } catch (Exception e) {
            throw new CommentExceptions.CommentFetchException("Failed to fetch comments by SOP Document ID: " + e.getMessage());
        }
    }

    // Search for comments by keyword
    public List<CommentResponseDTO> searchCommentsByKeyword(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("Search keyword cannot be null or empty");
            }

            List<Comment> comments = commentRepository.findByContentContaining(keyword);
            return comments.stream()
                    .map(commentMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CommentExceptions.InvalidSearchParameterException("Invalid search parameter: " + e.getMessage());
        } catch (Exception e) {
            throw new CommentExceptions.CommentSearchException("Failed to search comments: " + e.getMessage());
        }
    }
}
