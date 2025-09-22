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

    /** 유효 코드 → 매칭/신호 크레딧을 각각 +5 증가 후 현재 수량만 응답 */
    @Transactional
    public Map<String, Object> redeem(Long userId, String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase();
        if (normalized.isBlank()) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        int changed = codeRepo.markUsedIfUsable(normalized, LocalDateTime.now());
        if (changed == 0) throw new ApiException(ErrorCode.COUPON_INVALID_OR_EXPIRED);

        var user = userRepo.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        final int INC_MATCH = 5;
        final int INC_SIGNAL = 5;

        int newMatch  = Math.max(0, user.getMatchCredits())  + INC_MATCH;
        int newSignal = Math.max(0, user.getSignalCredits()) + INC_SIGNAL;

        user.setMatchCredits(newMatch);
        user.setSignalCredits(newSignal);
        userRepo.save(user); // 명시적으로 저장

        // ✅ 응답은 딱 두 개만
        return Map.of(
                "matchCredits",  user.getMatchCredits(),
                "signalCredits", user.getSignalCredits()
        );
    }
}