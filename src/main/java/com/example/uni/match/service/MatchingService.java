package com.example.uni.match.service;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.match.domain.MatchRequest;
import com.example.uni.match.domain.MatchResult;
import com.example.uni.match.domain.MatchStatus;
import com.example.uni.match.dto.MatchResultResponse;
import com.example.uni.match.policy.CandidatePipeline;
import com.example.uni.match.policy.ExclusionPolicy;
import com.example.uni.match.policy.FallbackSelector;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserCandidateRepository;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final UserCandidateRepository userCandidateRepository;
    private final com.example.uni.match.repo.MatchRequestRepository requestRepo;
    private final com.example.uni.match.repo.MatchResultRepository resultRepo;

    @Transactional
    public MatchResultResponse requestMatch(UUID meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (me.getMatchCredits() < 1) throw new ApiException(ErrorCode.QUOTA_EXCEEDED);
        me.setMatchCredits(me.getMatchCredits() - 1); // 원자적 차감(@Version)

        Gender targetGender = (me.getGender() == Gender.MALE) ? Gender.FEMALE : Gender.MALE;
        List<User> pool = userCandidateRepository
                .findByGenderAndProfileCompleteTrueAndIdNot(targetGender, me.getId());
        if (pool.size() < 3) throw new ApiException(ErrorCode.CONFLICT); // 물리 전제

        // 스코어링(내 이상형 대비 일치수 계산, 내림차순 정렬)
        List<CandidatePipeline.Scored> scored = CandidatePipeline.run(me, pool);

        // 사용자별 hit 캐시 (재계산 방지)
        Map<UUID, Integer> hitMap = scored.stream()
                .collect(Collectors.toMap(s -> s.user().getId(), CandidatePipeline.Scored::hit, (a, b) -> a));

        // Tier2(>=2), Tier1(=1), Tier0(=0)
        List<User> tier2 = scored.stream().filter(s -> s.hit() >= 2).map(CandidatePipeline.Scored::user).toList();
        List<User> tier1 = scored.stream().filter(s -> s.hit() == 1).map(CandidatePipeline.Scored::user).toList();
        List<User> tier0 = scored.stream().filter(s -> s.hit() == 0).map(CandidatePipeline.Scored::user).toList();

        // 첫 addAll() 경고 제거: 생성자에서 초기화
        List<User> pick = new ArrayList<>(tier2.stream().limit(3).toList());
        if (pick.size() < 3) pick.addAll(tier1.stream().limit(3 - pick.size()).toList());
        if (pick.size() < 3) {
            // 이미 뽑힌 후보/본인 제외하고 0-hit에서 채우기
            Set<UUID> exclude = ExclusionPolicy.buildDefaults(me.getId());
            exclude.addAll(pick.stream().map(User::getId).toList());
            pick.addAll(FallbackSelector.pick(tier0, exclude, 3 - pick.size()));
        }

        int ruleHit = pick.stream().mapToInt(u -> hitMap.getOrDefault(u.getId(), 0)).max().orElse(0);

        MatchRequest req = requestRepo.save(
                MatchRequest.builder().requester(me).ruleHit(ruleHit).build()
        );

        // 결과 저장
        for (User c : pick) {
            int hit = hitMap.getOrDefault(c.getId(), 0);
            resultRepo.save(MatchResult.builder()
                    .request(req).candidate(c).hitCount(hit).status(MatchStatus.CREATED).build());
        }

        // 응답 변환 (Map.of 대신 HashMap으로 명시 생성 → 제네릭 경고 제거)
        List<Map<String, Object>> candidates = pick.stream().map(u -> {
            Map<String, Object> row = new HashMap<>();
            row.put("userId", u.getId());
            row.put("nickname", u.getNickname());
            row.put("hit", hitMap.getOrDefault(u.getId(), 0));
            return row;
        }).collect(Collectors.toList());

        return MatchResultResponse.builder()
                .ruleHit(ruleHit)
                .candidates(candidates)
                .build();
    }

}
