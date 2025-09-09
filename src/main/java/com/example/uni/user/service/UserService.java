package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User get(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    public boolean isNameAvailable(String name) {
        return !userRepository.existsByNameIgnoreCase(name);
    }

    /** 온보딩 */
    @Transactional
    public User completeProfile(UUID userId, ProfileOnboardingRequest p) {
        User u = get(userId);

        if (p.getName() != null) {
            userRepository.findByNameIgnoreCase(p.getName()).ifPresent(other -> {
                if (!other.getId().equals(userId)) throw new ApiException(ErrorCode.CONFLICT);
            });
            u.setName(p.getName());
        }

        u.setDepartment(p.getDepartment());
        u.setStudentNo(p.getStudentNo());
        u.setBirthYear(p.getBirthYear());
        u.setProfileComplete(true);
        if (u.getMatchCredits() <= 0) u.setMatchCredits(2);
        return userRepository.save(u);
    }

    /** 성별 1회 지정 */
    @Transactional
    public void setGender(UUID userId, Gender gender){
        User u = get(userId);
        if (u.getGender() != null && u.getGender() != gender) {
            throw new ApiException(ErrorCode.CONFLICT);
        }
        u.setGender(gender);
    }

    /** 공통: 나이 계산 */
    private static Integer ageOf(User u){
        Integer by = u.getBirthYear();
        return (by == null) ? null : (Year.now().getValue() - by);
    }

    /** 내 프로필/상세 응답 DTO 매핑 (요약 포함) */
    public UserProfileResponse toResponse(User u){
        return UserProfileResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())
                .age(ageOf(u))
                .gender(u.getGender())
                .profileComplete(u.isProfileComplete())
                .matchCredits(u.getMatchCredits())
                .styleSummary(u.getStyleSummary())
                .build();
    }

    /** 목록/후보/신호/매칭 카드용 — 요약 미포함 */
    public Map<String,Object> toPublicCard(User u){
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("department", u.getDepartment());
        card.put("studentNo", u.getStudentNo());
        Integer age = ageOf(u);
        if (age != null) card.put("age", age);
        return card;
    }

    /** 상세보기 카드용 — 요약 포함 */
    public Map<String,Object> toDetailCard(User u){
        Map<String,Object> card = toPublicCard(u);
        card.put("styleSummary", u.getStyleSummary());
        return card;
    }

    @Transactional
    public void save(User u){ userRepository.save(u); }
}