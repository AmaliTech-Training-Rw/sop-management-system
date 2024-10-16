package com.team.authentication_service.repositories;

import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    public Optional<Department> findByName(String name);
}


// Have to think a lot about
// - user roles
// - emailing service