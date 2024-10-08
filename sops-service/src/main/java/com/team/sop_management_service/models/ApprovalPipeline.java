package com.team.sop_management_service.models;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPipeline {

    @DBRef
    private User author; // Staff assigned to create the SOP

    @DBRef
    private List<User> reviewers; // Staff assigned to review the SOP

    @DBRef
    private User approver; // Staff assigned to approve the SOP (can be HoD)

//    @DBRef
//    private User department;
}
