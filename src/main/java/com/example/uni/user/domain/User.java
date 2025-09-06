package com.example.uni.user.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_gender", columnList = "gender"),
                @Index(name = "idx_users_department", columnList = "department")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_kakao_id", columnNames = "kakao_id")
        }
)
@NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    /** 온보딩 전 가입 허용을 위해 null 허용 */
    @Enumerated(EnumType.STRING)
    private Gender gender;              // nullable

    private String email;

    // === 프로필 (온보딩 전 null 허용) ===
    private String name;                // nullable
    private String department;          // nullable

    @Column(name = "student_no")
    private String studentNo;           // nullable

    private Integer age;                // nullable

    // 성향(JSON) 저장용
    @Column(name = "traits_json", columnDefinition = "TEXT")
    private String traitsJson;

    @Column(name = "profile_complete", nullable = false)
    private boolean profileComplete;

    @Column(name = "match_credits", nullable = false)
    private int matchCredits;

    @Version
    private Long version;
}
