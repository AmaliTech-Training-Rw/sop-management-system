package com.team.authentication_service.repositories;

import com.team.authentication_service.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface  UserRoleRepository extends JpaRepository<UserRole, Integer> {
    @Query("SELECT ur FROM UserRole ur JOIN ur.user u JOIN ur.role r WHERE u.id = :userId")
    List<UserRole>  findByUserId(@Param("userId") int userId);}
