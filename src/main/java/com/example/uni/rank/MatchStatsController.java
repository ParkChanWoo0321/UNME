package com.example.uni.rank;

import com.example.uni.match.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MatchStatsController {

    private final MatchingService matchingService;

    @GetMapping("/stats/rank/department-matches")
    public ResponseEntity<?> departmentMatchRanking(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(java.util.Map.of("ranking", matchingService.rankDepartmentMatches(limit)));
    }

    @GetMapping("/stats/rank/mbti-signals")
    public ResponseEntity<?> mbtiSignalsRanking(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(java.util.Map.of("ranking", matchingService.rankMbtiBySignals(limit)));
    }

    @GetMapping("/stats/rank/mbti-matches")
    public ResponseEntity<?> mbtiMatchesRanking(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(java.util.Map.of("ranking", matchingService.rankMbtiByMatches(limit)));
    }
}
