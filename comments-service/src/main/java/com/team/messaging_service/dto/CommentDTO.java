package com.team.messaging_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDTO {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    @NotBlank(message = "UserId is required")
    private String userId;

    @NotBlank(message = "SOP Document ID is required")
    private String sopDocumentId;
}
