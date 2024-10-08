package com.team.sop_management_service.repository;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.models.SOP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface SOPRepository extends MongoRepository<SOP, String> {
    List<SOP> findByVisibility(Visibility visibility);
    List<SOP> findByStatus(SOPStatus status);
    List<SOP> findByApprovalPipeline_Author_Id(String authorId);
}