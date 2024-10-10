package com.team.sop_management_service.dto;

import com.team.sop_management_service.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SOPInitiationDTO {
    @NotBlank(message = "SOP title is mandatory")
    private String title;

    @NotNull(message = "Visibility is mandatory")
    private Visibility visibility;

    @NotNull(message = "Approval pipeline is mandatory")
    private ApprovalPipelineDTO approvalPipeline;

    @Data
    public static class ApprovalPipelineDTO {
        @NotNull(message = "Author is mandatory")
        private UserDTO author;

        @NotNull(message = "Approver is mandatory")
        private UserDTO approver;

        @NotNull(message = "At least one reviewer is required")
        private List<UserDTO> reviewers;
    }

    @Data
    public static class UserDTO {
        @NotBlank(message = "User ID is mandatory")
        private String id;

        @NotBlank(message = "Department is mandatory")
        private String department;
    }
}