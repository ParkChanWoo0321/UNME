package com.example.uni.event;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update VerifyCode v
              set v.used = true, v.usedAt = :now
            where v.code = :code
              and v.used = false
           """)
    int markUsedIfUsable(@Param("code") String code,
                         @Param("now") LocalDateTime now);
}
