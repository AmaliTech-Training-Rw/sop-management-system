package com.team.messaging_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String content;
    private String userId;
    private String sopDocumentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor to ensure timestamps are properly set
    public Comment(String content, String userId, String sopDocumentId) {
        this.content = content;
        this.userId = userId;
        this.sopDocumentId = sopDocumentId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
