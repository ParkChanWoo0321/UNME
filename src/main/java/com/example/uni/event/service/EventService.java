package com.example.uni.event.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.event.domain.VerifyCode;
import com.example.uni.event.repo.VerifyCodeRepository;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final VerifyCodeRepository codeRepo;
    private final UserRepository userRepo;

    /** DB에 미리 저장된 코드가 맞으면 3크레딧 지급(1회성) */
    @Transactional
    public Map<String, Object> redeem(UUID userId, String code){
        if (code == null || code.isBlank()) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        VerifyCode vc = codeRepo.findUsableForUpdate(code.trim(), LocalDateTime.now())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND)); // 코드 없음/만료/이미 사용

        vc.setUsed(true);
        vc.setUsedAt(LocalDateTime.now());

        var user = userRepo.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        user.setMatchCredits(user.getMatchCredits() + 3);

        return Map.of("ok", true, "creditsAfter", user.getMatchCredits());
    }
}
