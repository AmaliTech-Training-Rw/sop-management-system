package com.team.sop_management_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SOPCreationDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Content is required")
    private String content;  // Will store rich-text content as HTML

    @NotNull(message = "Version is required")
    private Integer version;

    private String category;
    private String subCategory;

    private List<Integer> reviewUserIds;  // List of user IDs for reviews

    private String sopInitiationId;  // Reference to SOPInitiation by ID

    private String sopReferenceId;  // Unique SOP reference ID for versioning
}
