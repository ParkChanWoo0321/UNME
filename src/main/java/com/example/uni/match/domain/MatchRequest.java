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
public class MatchRequest extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) private User requester;
    private int ruleHit; // 2|1|0
}
