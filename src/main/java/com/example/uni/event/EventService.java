package com.example.uni.event;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
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

    /** DB에 미리 저장된 코드가 맞으면 3크레딧 지급(1회성, 선착순 1명만 성공) */
    @Transactional
    public Map<String, Object> redeem(UUID userId, String code){
        String normalized = code == null ? "" : code.trim().toUpperCase();
        if (normalized.isBlank()) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        // 선착순 1명만 성공: 변경된 row 수가 1이어야 함
        int changed = codeRepo.markUsedIfUsable(normalized, LocalDateTime.now());
        if (changed == 0) throw new ApiException(ErrorCode.NOT_FOUND); // 없음/이미 사용됨

        var user = userRepo.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        user.setMatchCredits(user.getMatchCredits() + 3);

        return Map.of("ok", true, "creditsAfter", user.getMatchCredits());
    }
}
