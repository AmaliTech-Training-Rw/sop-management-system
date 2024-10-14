package com.team.sop_management_service.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;

    private Integer reviewerId;  // ID of the user who reviewed the SOP
    private boolean isConfirmed;  // Whether the review is a confirmation or return

}
