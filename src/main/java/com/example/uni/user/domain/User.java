// user/domain/User.java
package com.example.uni.user.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "uk_kakao_id", columnList = "kakaoId", unique = true),
        @Index(name = "idx_users_gender", columnList = "gender"),
        @Index(name = "idx_users_department", columnList = "department")
})
@NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String email;

    // === 프로필: 이름/학과/학번/나이 ===
    @Column(nullable = false)
    private String name;        // 이름

    @Column(nullable = false)
    private String department;  // 학과

    @Column(nullable = false)
    private String studentNo;   // 학번

    @Column(nullable = false)
    private Integer age;        // 나이

    // 온보딩 및 매칭 크레딧
    @Column(nullable = false)
    private boolean profileComplete;

    @Column(nullable = false)
    private int matchCredits;

    @Version private Long version;
}