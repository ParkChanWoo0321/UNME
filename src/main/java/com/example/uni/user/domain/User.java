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
                @Index(name = "idx_users_department", columnList = "department"),
                @Index(name = "uk_users_name", columnList = "name", unique = true)
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
    private Gender gender;

    @Column(name = "name", length = 8)
    private String name;

    private String department;

    @Column(name = "student_no")
    private String studentNo;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "profile_complete", nullable = false)
    private boolean profileComplete;

    @Column(name = "match_credits", nullable = false)
    private int matchCredits;

    @Version
    private Long version;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "dating_style_answers_json", columnDefinition = "TEXT")
    private String datingStyleAnswersJson;

    @Column(name = "dating_style_completed", nullable = false, columnDefinition = "boolean default false")
    private boolean datingStyleCompleted;

    @Column(name = "dating_style_summary", columnDefinition = "TEXT")
    private String styleSummary;
}
