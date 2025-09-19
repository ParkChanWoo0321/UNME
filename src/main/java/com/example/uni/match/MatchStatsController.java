package com.example.uni.match;

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
}
