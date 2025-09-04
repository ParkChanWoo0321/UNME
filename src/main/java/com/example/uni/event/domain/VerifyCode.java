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
public class VerifyCode extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(nullable=false, unique=true)
    private String code;

    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime usedAt;
}
