package com.example.uni.user.service;

import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> egenTeto() {
        long total = userRepository.countByDeactivatedAtIsNull();
        Map<String, Long> map = new HashMap<>();
        for (Object[] r : userRepository.countActiveByEgenType()) {
            String type = (String) r[0];
            long cnt = ((Number) r[1]).longValue();
            map.put(type, cnt);
        }
        long egen = map.getOrDefault("EGEN", 0L);
        long teto = map.getOrDefault("TETO", 0L);
        return Map.of(
                "total", total,
                "egen", Map.of("count", egen, "pct", pct(egen, total)),
                "teto", Map.of("count", teto, "pct", pct(teto, total))
        );
    }

    private double pct(long cnt, long total) {
        if (total == 0) return 0.0;
        return Math.round(cnt * 10000.0 / total) / 100.0;
    }
}
