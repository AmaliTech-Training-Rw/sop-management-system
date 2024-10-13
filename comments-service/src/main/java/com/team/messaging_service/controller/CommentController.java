package com.team.messaging_service.controller;

import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.dto.CommentResponseDTO;
import com.team.messaging_service.dto.ErrorResponse;
import com.team.messaging_service.dto.PagedResponse;
import com.team.messaging_service.exceptions.CommentExceptions;
import com.team.messaging_service.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        CommentResponseDTO response = commentService.createComment(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<CommentResponseDTO>> getLatestComments() {
        List<CommentResponseDTO> latestComments = commentService.getLatestComments();
        return ResponseEntity.ok(latestComments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable String id) {
        CommentResponseDTO response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sop/{sopDocumentId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsBySopDocumentId(@PathVariable String sopDocumentId) {
        List<CommentResponseDTO> comments = commentService.getCommentsBySopDocumentId(sopDocumentId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommentResponseDTO>> searchCommentsByKeyword(@RequestParam String keyword) {
        List<CommentResponseDTO> comments = commentService.searchCommentsByKeyword(keyword);
        return ResponseEntity.ok(comments);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CommentResponseDTO>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<CommentResponseDTO> pagedResponse = commentService.getAllComments(page, size);
        return ResponseEntity.ok(pagedResponse);
    }

    // Exception Handlers
    @ExceptionHandler(CommentExceptions.CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentExceptions.CommentNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CommentExceptions.InvalidCommentDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCommentData(CommentExceptions.InvalidCommentDataException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
