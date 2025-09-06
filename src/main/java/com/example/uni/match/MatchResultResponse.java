package com.example.uni.match;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class MatchResultResponse {
    private int ruleHit;
    private List<Map<String, Object>> candidates;
}
