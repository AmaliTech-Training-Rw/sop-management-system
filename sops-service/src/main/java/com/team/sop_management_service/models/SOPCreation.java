package com.team.sop_management_service.models;

import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.enums.SOPStatus;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "sop_created_documents")
public class SOPCreation {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Content is required")
    private String content;  // Will store rich-text content as HTML

    @NotNull(message = "Version is required")
    private Integer version;

    private String category;
    private String subCategory;

    @NotNull(message = "Status is required")
    private SOPStatus status;  // Enum to track draft, submitted, approved, etc.

    private List<UserDto> reviews;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean approved;

    @DBRef
    private SOPInitiation sopInitiation;  // Reference to SOPInitiation document

    private String sopReferenceId;  // Unique SOP reference ID for versioning

    private Boolean isCurrentVersion;

}
