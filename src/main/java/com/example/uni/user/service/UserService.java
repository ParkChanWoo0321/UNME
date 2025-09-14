package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.ai.TextGenClient;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.DatingStyleSummary;
import com.example.uni.user.dto.PeerDetailResponse;
import com.example.uni.user.dto.UserOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextGenClient textGenClient;
    private final ObjectMapper om;

    // 타입별 프로필 이미지 URL (properties에서 주입) — 기본값을 비워서 부팅 실패 방지
    @Value("${app.type-image.1:}")
    private String typeImage1;
    @Value("${app.type-image.2:}")
    private String typeImage2;
    @Value("${app.type-image.3:}")
    private String typeImage3;
    @Value("${app.type-image.4:}")
    private String typeImage4;

    private String imageUrlByType(int typeId){
        return switch (typeId) {
            case 1 -> typeImage1;
            case 2 -> typeImage2;
            case 3 -> typeImage3;
            default -> typeImage4;
        };
    }

    public User get(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean isNameAvailable(String name) {
        return !userRepository.existsByNameIgnoreCase(name);
    }

    /** A/B 개수로 최종 타입 산출 (규칙 유지) */
    private static int determineTypeId(Map<String,String> answers) {
        int aCnt = 0;
        for (int i = 1; i <= 10; i++) {
            String v = Optional.ofNullable(answers.get("q"+i)).orElse("").trim().toLowerCase();
            if ("a".equals(v)) aCnt++;
        }
        int bCnt = 10 - aCnt;

        if (aCnt >= 7) return 1;   // 활발한 에너지
        if (bCnt >= 7) return 2;   // 따뜻한 통찰력
        if (aCnt == 5) return 4;   // 5:5 동률은 4번 고정
        if (aCnt <= 4) return 3;   // 든든한 신뢰
        return 4;                  // 세련된 감각
    }

    /** typeId → 제목/내용 텍스트 매핑 (응답에만 사용) */
    private static TypeText toTypeText(int typeId){
        return switch (typeId) {
            case 1 -> new TypeText("활발한 에너지형 입니다.", "관계의 즐거움과 생기를 붙여넣는 매력의 소유자!");
            case 2 -> new TypeText("따뜻한 통찰력형 입니다.", "깊은 교감의 매력 소유자!");
            case 3 -> new TypeText("든든한 신뢰형 입니다.", "관계의 일정을 주는 든든한 매력의 소유자!");
            default -> new TypeText("세련된 감각형 입니다.", "만남을 빛내는 개성 넘치는 매력의 소유자!");
        };
    }
    private record TypeText(String title, String content) {}

    /** 기본정보 + 성별 + 성향테스트 (한 번에 완료) */
    @Transactional
    public User completeProfile(UUID userId, UserOnboardingRequest req) {
        User u = get(userId);

        if (req.getName() != null) {
            userRepository.findByNameIgnoreCase(req.getName()).ifPresent(other -> {
                if (!other.getId().equals(userId)) throw new ApiException(ErrorCode.CONFLICT);
            });
            u.setName(req.getName());
        }

        u.setDepartment(req.getDepartment());
        u.setStudentNo(req.getStudentNo());
        u.setBirthYear(req.getBirthYear());

        if (u.getGender() != null && u.getGender() != req.getGender()) {
            throw new ApiException(ErrorCode.CONFLICT);
        }
        u.setGender(req.getGender());

        // 성향답변 수집
        Map<String,String> answers = new LinkedHashMap<>();
        answers.put("q1", req.getQ1()); answers.put("q2", req.getQ2());
        answers.put("q3", req.getQ3()); answers.put("q4", req.getQ4());
        answers.put("q5", req.getQ5()); answers.put("q6", req.getQ6());
        answers.put("q7", req.getQ7()); answers.put("q8", req.getQ8());
        answers.put("q9", req.getQ9()); answers.put("q10", req.getQ10());

        // 내부 저장용 typeId 산출(응답에는 노출 안 함)
        u.setTypeId(determineTypeId(answers));

        // 답변 변경 시 특징 요약 재생성(특징만 DB에 저장 유지)
        try {
            String newJson = om.writeValueAsString(answers);
            String oldJson = Optional.ofNullable(u.getDatingStyleAnswersJson()).orElse("");
            if (!newJson.equals(oldJson)) {
                DatingStyleSummary ds = textGenClient.summarizeDatingStyle(answers);
                u.setStyleSummary(ds.getFeature());
            }
            u.setDatingStyleAnswersJson(newJson);
        } catch (Exception e) {
            u.setDatingStyleAnswersJson("{}");
        }

        u.setProfileComplete(true);
        if (u.getMatchCredits() <= 0) u.setMatchCredits(2);

        return userRepository.save(u);
    }

    @Transactional
    public User updateIntroduce(UUID userId, String introduce) {
        User u = get(userId);
        u.setIntroduce(introduce);
        return userRepository.save(u);
    }

    private Map<String,String> parseAnswers(String json){
        try {
            if (json == null || json.isBlank()) return Collections.emptyMap();
            return om.readValue(json, new TypeReference<Map<String,String>>(){});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /** 내 프로필 응답 DTO 매핑 (typeId 미노출 + 이미지 URL 포함) */
    public UserProfileResponse toResponse(User u){
        int typeId = Optional.ofNullable(u.getTypeId()).orElse(4);
        TypeText tt = toTypeText(typeId);

        Map<String,String> answers = parseAnswers(u.getDatingStyleAnswersJson());
        DatingStyleSummary ds = answers.isEmpty() ? null : textGenClient.summarizeDatingStyle(answers);

        return UserProfileResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())
                .gender(u.getGender())
                .profileComplete(u.isProfileComplete())
                .matchCredits(u.getMatchCredits())
                .typeTitle(tt.title())
                .typeContent(tt.content())
                .typeImageUrl(imageUrlByType(typeId)) // 프로퍼티 미설정 시 빈 문자열
                .styleSummary(u.getStyleSummary())
                .recommendedPartner(ds != null ? ds.getRecommendedPartner() : null)
                .tags(ds != null ? ds.getTags() : List.of())
                .introduce(u.getIntroduce())
                .build();
    }

    /** 상대 상세 응답 DTO 매핑 (typeId 미노출 + 이미지 URL 포함) */
    public PeerDetailResponse toPeerResponse(User u){
        int typeId = Optional.ofNullable(u.getTypeId()).orElse(4);
        TypeText tt = toTypeText(typeId);

        Map<String,String> answers = parseAnswers(u.getDatingStyleAnswersJson());
        DatingStyleSummary ds = answers.isEmpty() ? null : textGenClient.summarizeDatingStyle(answers);

        return PeerDetailResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())
                .gender(u.getGender())
                .typeTitle(tt.title())
                .typeContent(tt.content())
                .typeImageUrl(imageUrlByType(typeId))
                .styleSummary(u.getStyleSummary())
                .recommendedPartner(ds != null ? ds.getRecommendedPartner() : null)
                .tags(ds != null ? ds.getTags() : List.of())
                .introduce(u.getIntroduce())
                .build();
    }
}