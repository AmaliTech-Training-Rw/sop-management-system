package com.team.messaging_service.model;

import lombok.AllArgsConstructor;
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
@Document(collection = "comments")
public class Comment {
    @Id
    private String id; // Unique identifier for the comment
    private String content; // Content of the comment
    private String userId; // ID of the user who made the comment
    private String sopDocumentId; // ID of the associated SOP document
    private LocalDateTime createdAt; // Timestamp when the comment was created

    // Constructor to create a new comment with the current timestamp
    public Comment(String content, String userId, String sopDocumentId) {
        this.content = content;
        this.userId = userId;
        this.sopDocumentId = sopDocumentId;
        this.createdAt = LocalDateTime.now(); // Set current timestamp
    }
}
