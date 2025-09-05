// user/service/UserService.java
package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User get(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
    }

    /** 온보딩: 이름/학과/학번/나이 저장 + 프로필 완료 + 크레딧 2 세팅 */
    @Transactional
    public User completeProfile(UUID userId, ProfileOnboardingRequest p) {
        User u = get(userId);
        u.setName(p.getName());
        u.setDepartment(p.getDepartment());
        u.setStudentNo(p.getStudentNo());
        u.setAge(p.getAge());

        u.setProfileComplete(true);
        // 최초 온보딩 시 2로 세팅(이미 값이 있다면 유지하고 싶으면 조건 걸 것)
        if (u.getMatchCredits() <= 0) {
            u.setMatchCredits(2);
        }
        return userRepository.save(u);
    }

    public UserProfileResponse toResponse(User u){
        return UserProfileResponse.builder()
                .userId(u.getId())
                .name(u.getName())
                .department(u.getDepartment())
                .studentNo(u.getStudentNo())
                .age(u.getAge())
                .gender(u.getGender())
                .profileComplete(u.isProfileComplete())
                .matchCredits(u.getMatchCredits())
                .build();
    }
}