package com.team.messaging_service.service;

import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.dto.CommentResponseDTO;
import com.team.messaging_service.model.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toEntity(CommentDTO dto) {
        return Comment.builder()
                .content(dto.getContent())
                .userId(dto.getUserId())
                .sopDocumentId(dto.getSopDocumentId())
                .createdAt(LocalDateTime.now())  // Ensure correct timestamps
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CommentResponseDTO toResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .sopDocumentId(comment.getSopDocumentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
