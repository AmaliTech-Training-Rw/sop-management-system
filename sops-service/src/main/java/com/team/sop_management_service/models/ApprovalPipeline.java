package com.team.sop_management_service.models;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPipeline {

    //@DBRef
    private String author; // Staff assigned to create the SOP

    //@DBRef
    private List<String> reviewers; // Staff assigned to review the SOP

    //@DBRef
    private String approver; // Staff assigned to approve the SOP (can be HoD)

   // @DBRef
    private String department;
}
