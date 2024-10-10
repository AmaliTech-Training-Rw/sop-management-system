package com.team.sop_management_service.repository;

import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.models.SOPCreation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SOPCreationRepository extends MongoRepository<SOPCreation, String> {
    // Find SOPs by title (case-insensitive)
    List<SOPCreation> findByTitleIgnoreCase(String title);

    // Find SOPs by approval status
    List<SOPCreation> findByApproved(Boolean approved);

    // Find SOP by reference ID
    Optional<SOPCreation> findBySopReferenceId(String sopReferenceId);
//
//    List<SOPCreationDTO> findBySopReferenceIdOrderByVersionDesc(String sopReferenceId);
//    Optional<SOPCreationDTO> findBySopReferenceIdAndVersion(String sopReferenceId, int version);
//    Optional<SOPCreationDTO> findBySopReferenceIdAndIsCurrentVersionTrue(String sopReferenceId);
}