package com.team.sop_management_service.models;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.enums.Visibility;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sop_service")
public class SOP {

    @Id
    private String sopId; // In MongoDB, we usually use String for ID.

    @NotBlank(message = "SOP title is mandatory")
    private String title;

    private Visibility visibility;

    @DBRef // Reference to the User entity for the author
    private User author;

    @DBRef // Reference to the User entity for reviewers
    private List<User> reviewers;

    @DBRef // Reference to the User entity for the approver
    private User approver;

    @DBRef // Reference to the Department entity
    private Department department;

    private SOPStatus status;
}
