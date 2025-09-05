package com.example.uni.event.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@Entity
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "verify_code",
        indexes = { @Index(name = "uk_verify_code_code", columnList = "code", unique = true) })
public class VerifyCode extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    /** 미리 DB에 넣어둘 코드(유니크) */
    @Column(nullable = false, unique = true)
    private String code;

    /** (선택) 만료 시각. null이면 만료 없음 */
    private LocalDateTime expiresAt;

    /** 사용 여부/사용 시각 */
    private boolean used;
    private LocalDateTime usedAt;
}
