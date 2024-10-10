package com.team.sop_management_service.dto;

// DTOs
import com.team.sop_management_service.enums.SOPStatus;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SOPCreationDTO {
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Content is required")
    private String content;

    private Integer version;

    private String category;
    private String subCategory;

    @NotNull(message = "Status is required")
    private SOPStatus status;

    private String sopReferenceId;
    private String createdBy;
    private String updatedBy;
}
