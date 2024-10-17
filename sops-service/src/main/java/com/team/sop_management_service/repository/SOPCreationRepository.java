package com.team.sop_management_service.repository;

import com.team.sop_management_service.models.SOPCreation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SOPCreationRepository extends MongoRepository<SOPCreation, String> {

    List<SOPCreation> findBySopReferenceIdOrderByVersionDesc(String sopReferenceId);

    Optional<SOPCreation> findBySopReferenceIdAndIsCurrentVersionTrue(String sopReferenceId);

    Optional<SOPCreation> findBySopReferenceIdAndVersion(String sopReferenceId, int version);

    Page<SOPCreation> findBySopReferenceId(String sopReferenceId, Pageable pageable);
}
