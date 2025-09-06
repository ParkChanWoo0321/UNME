package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper om;

    public User get(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    /** 온보딩: 닉네임/학과/학번/출생연도 저장 + 프로필 완료 + 크레딧 2 세팅(없을 때만) */
    @Transactional
    public User completeProfile(UUID userId, ProfileOnboardingRequest p) {
        User u = get(userId);
        u.setName(p.getName());
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

    /** 성향 저장(JSON) */
    @Transactional
    public void saveTraits(UUID userId, Object traitsDto){
        User u = get(userId);
        try {
            String json = (traitsDto instanceof String)
                    ? (String) traitsDto
                    : om.writeValueAsString(traitsDto);
            u.setTraitsJson(json);
        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }
    }

    public UserProfileResponse toResponse(User u){
        Map<String,Object> traits = null;
        if (u.getTraitsJson() != null && !u.getTraitsJson().isBlank()) {
            try { traits = om.readValue(u.getTraitsJson(), Map.class); }
            catch (Exception ignored) { traits = new HashMap<>(); }
        }
        Integer age = (u.getBirthYear() == null) ? null : (Year.now().getValue() - u.getBirthYear());

        return UserProfileResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .birthYear(u.getBirthYear())
                .age(age) // 계산된 나이
                .gender(u.getGender())
                .profileComplete(u.isProfileComplete())
                .matchCredits(u.getMatchCredits())
                .profileImageUrl(u.getProfileImageUrl())
                .build();
    }

    @Transactional
    public User save(User u){ return userRepository.save(u); }
}
