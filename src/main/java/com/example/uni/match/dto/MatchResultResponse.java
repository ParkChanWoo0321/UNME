package com.example.uni.match.dto;

import lombok.Builder; import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter @Builder
public class MatchResultResponse {
    private int ruleHit; // 2|1|0
    private List<Map<String,Object>> candidates; // {userId,nickname,hit}
}
