package com.example.uni.event;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {
    private final VerifyCodeRepository codeRepo;
    private final UserRepository userRepo;

    /** 유효 코드 → 매칭/신호 크레딧을 모두 5로 설정 (1회성) */
    @Transactional
    public Map<String, Object> redeem(Long userId, String code) { // ← Long로 변경
        String normalized = code == null ? "" : code.trim().toUpperCase();
        if (normalized.isBlank()) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        int changed = codeRepo.markUsedIfUsable(normalized, LocalDateTime.now());
        if (changed == 0) throw new ApiException(ErrorCode.COUPON_INVALID_OR_EXPIRED);

        var user = userRepo.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        user.setMatchCredits(5);
        user.setSignalCredits(5);
        return Map.of("ok", true, "matchCredits", user.getMatchCredits(), "signalCredits", user.getSignalCredits());
    }
}
