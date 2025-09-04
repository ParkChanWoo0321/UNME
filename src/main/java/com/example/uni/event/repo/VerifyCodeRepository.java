package com.example.uni.event.repo;

import com.example.uni.event.domain.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, UUID> {
    Optional<VerifyCode> findByCodeAndUsedFalseAndExpiresAtAfter(String code, LocalDateTime now);
}
