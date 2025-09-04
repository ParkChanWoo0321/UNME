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
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final VerifyCodeRepository codeRepo;
    private final UserRepository userRepo;

    public String issueCode(int ttlMinutes){
        String code = randomCode(8);
        codeRepo.save(VerifyCode.builder()
                .code(code).expiresAt(LocalDateTime.now().plusMinutes(ttlMinutes)).used(false).build());
        return code;
    }

    @Transactional
    public int useCode(UUID userId, String code){
        VerifyCode vc = codeRepo.findByCodeAndUsedFalseAndExpiresAtAfter(code, LocalDateTime.now())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        vc.setUsed(true); vc.setUsedAt(LocalDateTime.now());
        var u = userRepo.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        u.setMatchCredits(u.getMatchCredits()+1);
        return u.getMatchCredits();
    }

    private static String randomCode(int len){
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
