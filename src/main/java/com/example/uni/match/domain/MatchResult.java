package com.example.uni.match.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import com.example.uni.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@NoArgsConstructor @AllArgsConstructor @Builder
public class MatchResult extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) private MatchRequest request;
    @ManyToOne(optional=false) private User candidate;
    private int hitCount; // 후보별 일치수
    @Enumerated(EnumType.STRING) private MatchStatus status;
}
