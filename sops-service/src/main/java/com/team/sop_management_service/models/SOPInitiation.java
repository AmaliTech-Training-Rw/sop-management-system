package com.team.sop_management_service.models;

import com.team.sop_management_service.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sop_initiation_documents")
public class SOPInitiation {

    @Id
    private String sopId;

    @NotBlank(message = "SOP title is mandatory")
    private String title;

    private Visibility visibility;

    private ApprovalPipeline approvalPipeline; // The pipeline of assigned users for approval
}
