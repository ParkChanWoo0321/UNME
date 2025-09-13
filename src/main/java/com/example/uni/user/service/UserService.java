package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.PeerDetailResponse;
import com.example.uni.user.dto.UserOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import com.example.uni.user.ai.TextGenClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextGenClient textGenClient;
    private final ObjectMapper om;

    public User get(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean isNameAvailable(String name) {
        return !userRepository.existsByNameIgnoreCase(name);
    }

    /** A/B 개수로 최종 타입 산출 */
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

        // 성향답변 항상 수집
        Map<String,String> answers = new LinkedHashMap<>();
        answers.put("q1", req.getQ1()); answers.put("q2", req.getQ2());
        answers.put("q3", req.getQ3()); answers.put("q4", req.getQ4());
        answers.put("q5", req.getQ5()); answers.put("q6", req.getQ6());
        answers.put("q7", req.getQ7()); answers.put("q8", req.getQ8());
        answers.put("q9", req.getQ9()); answers.put("q10", req.getQ10());

        // 타입 갱신
        u.setTypeId(determineTypeId(answers));

        // 답변 변경 시에만 요약 재생성
        try {
            String newJson = om.writeValueAsString(answers);
            String oldJson = Optional.ofNullable(u.getDatingStyleAnswersJson()).orElse("");
            if (!newJson.equals(oldJson)) {
                String summary = textGenClient.summarizeDatingStyle(answers);
                u.setStyleSummary(summary);
            }
            u.setDatingStyleAnswersJson(newJson);
        } catch (Exception e) {
            u.setDatingStyleAnswersJson("{}"); // 실패 시 기본값
        }

        // 프로필 완료
        u.setProfileComplete(true);
        if (u.getMatchCredits() <= 0) u.setMatchCredits(2);

        return userRepository.save(u);
    }

    /** 한 줄 소개 작성/수정 */
    @Transactional
    public User updateIntroduce(UUID userId, String introduce) {
        User u = get(userId);
        u.setIntroduce(introduce);
        return userRepository.save(u);
    }

    /** 내 프로필 응답 DTO 매핑 (두 자리 년생만) */
    public UserProfileResponse toResponse(User u){
        return UserProfileResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())   // "90" ~ "06"
                .gender(u.getGender())
                .profileComplete(u.isProfileComplete())
                .matchCredits(u.getMatchCredits())
                .typeId(u.getTypeId())
                .styleSummary(u.getStyleSummary())
                .introduce(u.getIntroduce())
                .build();
    }

    /** 상대 상세 응답 DTO 매핑 (두 자리 년생만) */
    public PeerDetailResponse toPeerResponse(User u){
        return PeerDetailResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())   // "90" ~ "06"
                .gender(u.getGender())
                .typeId(u.getTypeId())
                .introduce(u.getIntroduce())
                .build();
    }
}