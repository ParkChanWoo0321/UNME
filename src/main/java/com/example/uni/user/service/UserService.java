package com.example.uni.user.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.*;
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

    @Transactional
    public User completeProfile(UUID userId, ProfileOnboardingRequest p) {
        User u = get(userId);
        // 성별별 필드 강제
        if (u.getGender() == Gender.MALE) {
            if (p.getMaleHair()==null) throw new ApiException(ErrorCode.VALIDATION_ERROR);
            u.setMaleHair(p.getMaleHair()); u.setFemaleHair(null);
        } else {
            if (p.getFemaleHair()==null) throw new ApiException(ErrorCode.VALIDATION_ERROR);
            u.setFemaleHair(p.getFemaleHair()); u.setMaleHair(null);
        }
        u.setNickname(p.getNickname());
        u.setAge(p.getAge());
        u.setStudentId(p.getStudentId());
        u.setMajor(p.getMajor());
        u.setMbti(p.getMbti());
        u.setHeightBand(p.getHeightBand());
        u.setSelfIntro(p.getSelfIntro());
        userRepository.save(u);
        return u;
    }

    @Transactional
    public User completeIdeal(UUID userId, IdealOnboardingRequest r){
        User u = get(userId);
        // 반대 성별 필수
        if (u.getGender() == Gender.MALE) {
            if (r.getIdealFemaleHair()==null) throw new ApiException(ErrorCode.VALIDATION_ERROR);
            u.setIdealFemaleHair(r.getIdealFemaleHair()); u.setIdealMaleHair(null);
        } else {
            if (r.getIdealMaleHair()==null) throw new ApiException(ErrorCode.VALIDATION_ERROR);
            u.setIdealMaleHair(r.getIdealMaleHair()); u.setIdealFemaleHair(null);
        }
        u.setIdealMbti(r.getIdealMbti());
        u.setIdealHeightBand(r.getIdealHeightBand());
        u.setIdealAgePref(r.getIdealAgePref());
        // 두 섹션 완료되면 profileComplete=true
        u.setProfileComplete(u.getNickname()!=null && u.getIdealMbti()!=null);
        return userRepository.save(u);
    }

    public UserProfileResponse toResponse(User u){
        return UserProfileResponse.builder()
                .nickname(u.getNickname()).age(u.getAge()).studentId(u.getStudentId()).major(u.getMajor())
                .mbti(u.getMbti()).heightBand(u.getHeightBand()).selfIntro(u.getSelfIntro())
                .maleHair(u.getMaleHair()).femaleHair(u.getFemaleHair())
                .idealMbti(u.getIdealMbti()).idealHeightBand(u.getIdealHeightBand()).idealAgePref(u.getIdealAgePref())
                .idealMaleHair(u.getIdealMaleHair()).idealFemaleHair(u.getIdealFemaleHair())
                .profileComplete(u.isProfileComplete()).matchCredits(u.getMatchCredits())
                .build();
    }
}
