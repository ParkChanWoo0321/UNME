package com.example.uni.user.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "uk_kakao_id", columnList = "kakaoId", unique = true)
})
@NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    private String email;

    // 내 정보
    private String nickname;
    private Integer age;
    private String studentId;
    private String major;

    @Enumerated(EnumType.STRING) private Mbti mbti;
    @Enumerated(EnumType.STRING) private HeightBand heightBand;

    @Enumerated(EnumType.STRING) private MaleHair maleHair;        // 남: 사용
    @Enumerated(EnumType.STRING) private FemaleHair femaleHair;    // 여: 사용

    @Column(length = 200) private String selfIntro;                 // 한줄소개

    // 이상형(반대 성별)
    @Enumerated(EnumType.STRING) private Mbti idealMbti;
    @Enumerated(EnumType.STRING) private HeightBand idealHeightBand;
    @Enumerated(EnumType.STRING) private MaleHair idealMaleHair;       // 본인이 FEMALE일 때 사용
    @Enumerated(EnumType.STRING) private FemaleHair idealFemaleHair;   // 본인이 MALE일 때 사용
    @Enumerated(EnumType.STRING) private AgePref idealAgePref;

    private boolean profileComplete;
    private int matchCredits;

    @Version private Long version;
}
