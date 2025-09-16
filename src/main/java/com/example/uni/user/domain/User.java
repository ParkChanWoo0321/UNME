// com/example/uni/user/domain/User.java
package com.example.uni.user.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime; // ← 추가

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "name", length = 8)
    private String name;

    private String department;

    @Column(name = "student_no")
    private String studentNo;

    @Column(name = "birth_year", length = 4)
    private String birthYear;

    @Column(name = "profile_complete", nullable = false)
    private boolean profileComplete;

    @Column(name = "match_credits", nullable = false)
    private int matchCredits;

    @Column(name = "signal_credits", nullable = false)
    private int signalCredits;

    @Version
    private Long version;

    @Column(name = "dating_style_answers_json", columnDefinition = "TEXT")
    private String datingStyleAnswersJson;

    @Column(name = "dating_style_summary", columnDefinition = "TEXT")
    private String styleSummary;

    @Column(name = "dating_style_type_id")
    private Integer typeId;

    @Column(name = "introduce", length = 100)
    private String introduce;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "style_recommended_partner", length = 600)
    private String styleRecommendedPartner;

    @Column(name = "style_tags_json")
    private String styleTagsJson;

    /** 소프트탈퇴 시각(널이면 활성) */
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt; // ← 추가
}
