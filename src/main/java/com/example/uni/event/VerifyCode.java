package com.example.uni.event;

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

    /** 미리 DB에 넣어둘 코드(대문자 권장) */
    @Column(nullable = false)
    private String code;

    /** 1회성 사용 플래그 및 사용 시각 */
    private boolean used;
    private LocalDateTime usedAt;
}
