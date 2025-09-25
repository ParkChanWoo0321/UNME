package com.example.uni.match;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "seen_candidates",
        uniqueConstraints = @UniqueConstraint(name = "uk_seen_pair", columnNames = {"viewer_id", "seen_user_id"}),
        indexes = @Index(name = "idx_seen_viewer", columnList = "viewer_id"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeenCandidate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "viewer_id", nullable = false)
    private Long viewerId;

    @Column(name = "seen_user_id", nullable = false)
    private Long seenUserId;
}
