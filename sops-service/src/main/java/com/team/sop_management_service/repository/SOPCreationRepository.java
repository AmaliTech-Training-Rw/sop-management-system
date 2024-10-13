package com.team.sop_management_service.repository;

import com.team.sop_management_service.dto.SOPCreationDTO;
import com.team.sop_management_service.models.SOPCreation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
//
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


    List<SOPCreation> findBySopReferenceIdOrderByVersionDesc(String sopReferenceId);

    Optional<SOPCreation> findBySopReferenceIdAndIsCurrentVersionTrue(String sopReferenceId);

    Optional<SOPCreation> findBySopReferenceIdAndVersion(String sopReferenceId, int version);

    Page<SOPCreation> findBySopReferenceId(String sopReferenceId, Pageable pageable);
    // Custom query to fetch the latest version of an SOP based on ID
//    @Query("SELECT s FROM SOPCreation s WHERE s.sopReferenceId = :id ORDER BY s.version DESC")
//    SOPCreation findLatestById(@Param("id") String id);
    SOPCreation findTopBySopReferenceIdOrderByVersionDesc(String sopReferenceId);

}
