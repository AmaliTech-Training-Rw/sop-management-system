package com.team.sop_management_service.models;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPipeline {

    private int author; // Staff assigned to create the SOP

    private List<Integer> reviewers; // Staff assigned to review the SOP

    private int approver; // Staff assigned to approve the SOP (can be HoD)
}
