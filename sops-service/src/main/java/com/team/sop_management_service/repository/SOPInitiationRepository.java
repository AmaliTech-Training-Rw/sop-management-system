package com.team.sop_management_service.repository;

import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.models.SOPInitiation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface SOPInitiationRepository extends MongoRepository<SOPInitiation, String> {
    List<SOPInitiation> findByVisibility(Visibility visibility);
    //List<SOPInitiation> findByApprovalPipeline_Author_Id(int authorId);
}