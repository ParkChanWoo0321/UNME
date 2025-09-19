package com.example.uni.rank;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@Table(name = "match_logs",
        indexes = {
                @Index(name = "idx_match_dept_a", columnList = "department_a"),
                @Index(name = "idx_match_dept_b", columnList = "department_b")
        })
@NoArgsConstructor @AllArgsConstructor @Builder
public class MatchLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_a_id", nullable = false)
    private Long userAId;

    @Column(name = "user_b_id", nullable = false)
    private Long userBId;

    @Column(name = "department_a", nullable = false)
    private String departmentA;

    @Column(name = "department_b", nullable = false)
    private String departmentB;
}
