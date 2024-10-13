package com.team.authentication_service.repositories;

import com.team.authentication_service.models.Otp;
import com.team.authentication_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    @Query("SELECT o FROM Otp o WHERE o.user = :user ORDER BY o.createdAt DESC LIMIT 1 ")
    Otp findByUser(User user);
    Otp findByCode(String code);
}
