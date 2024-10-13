package com.team.sop_management_service.dto;

import com.team.sop_management_service.authenticationService.UserDto;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.models.ApprovalPipeline;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SOPInitiationDTO {

    private String sopId;

    @NotBlank(message = "SOP title is mandatory")
    private String title;

    private Visibility visibility;

    private ApprovalPipeline approvalPipeline; // List of user IDs or names in the approval pipeline

    public SOPInitiationDTO(String sopId, @NotBlank(message = "SOP title is mandatory") String title, Visibility visibility) {
    }
}
