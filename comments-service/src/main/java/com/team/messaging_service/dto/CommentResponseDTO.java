package com.team.messaging_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
public class CommentResponseDTO {
    private String id;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String userId;
    private String sopDocumentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
