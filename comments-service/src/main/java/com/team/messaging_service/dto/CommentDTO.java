package com.team.messaging_service.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private String content;
    private String userId;
    private String sopDocumentId;

}

