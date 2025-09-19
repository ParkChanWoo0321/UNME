package com.example.uni.user.service;

import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> egenTeto() {
        long total = userRepository.countByDeactivatedAtIsNull();
        long egen = 0, teto = 0;
        for (String raw : userRepository.findActiveEgenTypes()) {
            String key = normalize(raw);
            if ("EGEN".equals(key)) egen++;
            else if ("TETO".equals(key)) teto++;
        }
        return Map.of(
                "total", total,
                "egen", Map.of("count", egen, "pct", pct(egen, total)),
                "teto", Map.of("count", teto, "pct", pct(teto, total))
        );
    }

    private String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC);
        n = n.replaceAll("[^A-Za-z]", "");
        return n.toUpperCase(Locale.ROOT);
    }

    private double pct(long cnt, long total) {
        if (total == 0) return 0.0;
        return Math.round(cnt * 10000.0 / total) / 100.0;
    }
}
