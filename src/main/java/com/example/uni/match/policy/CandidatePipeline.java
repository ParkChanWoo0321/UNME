package com.example.uni.match.policy;

import com.example.uni.user.domain.User;

import java.util.*;
import java.util.stream.Collectors;

public class CandidatePipeline {

    public record Scored(User user, int hit){}

    public static List<Scored> run(User me, List<User> pool){
        // 스코어링
        List<Scored> scored = pool.stream()
                .map(u -> new Scored(u, MatchingPolicy.hitCount(me, u)))
                .collect(Collectors.toList());
        // hit desc, 최근활동은 생략(필요 시 컬럼 추가)
        scored.sort(Comparator.comparingInt(Scored::hit).reversed());
        return scored;
    }
}
