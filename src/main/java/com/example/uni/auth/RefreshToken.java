package com.example.uni.auth;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name="idx_refresh_user", columnList = "userId"),
                @Index(name="idx_refresh_device", columnList = "deviceId")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 64)
    private String deviceId;  // 클라이언트가 보내는 고정 UUID

    @Column(nullable = false, unique = true, length = 512)
    private String token;     // 서명된 문자열(또는 난수). 필요시 해시 저장.

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    public static RefreshToken issue(String userId, String deviceId, long ttlSeconds){
        return RefreshToken.builder()
                .userId(userId)
                .deviceId(deviceId)
                .token(generateSecureRandom()) // 또는 JWT 생성
                .expiresAt(Instant.now().plusSeconds(ttlSeconds))
                .revoked(false)
                .build();
    }

    public RefreshToken rotate(long ttlSeconds){
        return RefreshToken.issue(this.userId, this.deviceId, ttlSeconds);
    }

    public boolean isExpired(){ return Instant.now().isAfter(expiresAt); }
    public void revoke(){ this.revoked = true; }

    private static String generateSecureRandom(){
        return UUID.randomUUID().toString() + UUID.randomUUID(); // 예시. 실제로는 난수/JWT 권장
    }
}
