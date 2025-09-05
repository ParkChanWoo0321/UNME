package com.example.uni.event.repo;

import com.example.uni.event.domain.VerifyCode;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, UUID> {

    /** 코드 선점 잠금: 미사용 && (만료없음 또는 아직 유효) */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select v from VerifyCode v
           where v.code = :code
             and v.used = false
             and (v.expiresAt is null or v.expiresAt > :now)
           """)
    Optional<VerifyCode> findUsableForUpdate(@Param("code") String code,
                                             @Param("now") LocalDateTime now);
}
