// match/service/MatchingService.java
package com.example.uni.match.service;

import com.example.uni.chat.domain.ChatRoom;
import com.example.uni.chat.repo.ChatRoomRepository;
import com.example.uni.chat.service.ChatService;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.match.domain.Signal;
import com.example.uni.match.dto.MatchResultResponse;
import com.example.uni.match.repo.SignalRepository;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserCandidateRepository;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final UserCandidateRepository userCandidateRepository;
    private final SignalRepository signalRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /** 매칭 시작: 이성/다른 학과/본인 제외/기보낸신호 제외/기존채팅 없음 → 랜덤 3명 */
    @Transactional
    public MatchResultResponse requestMatch(UUID meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (me.getMatchCredits() < 1) throw new ApiException(ErrorCode.QUOTA_EXCEEDED);

        // 크레딧 차감(Optimistic Lock: @Version)
        me.setMatchCredits(me.getMatchCredits() - 1);

        Gender opposite = (me.getGender()==Gender.MALE) ? Gender.FEMALE : Gender.MALE;

        // 1차 풀(이성 + 같은 학과 제외 + 프로필완료 + 본인 제외)
        List<User> pool = userCandidateRepository
                .findByGenderAndDepartmentNotAndProfileCompleteTrueAndIdNot(opposite, me.getDepartment(), me.getId());

        // 제외 조건: 이미 신호 보냄 / 이미 방 있음
        Set<UUID> alreadySignaled = new HashSet<>();
        signalRepository.findAllBySender(me).forEach(s -> alreadySignaled.add(s.getReceiver().getId()));

        List<Map<String,Object>> candidates = new ArrayList<>();
        Collections.shuffle(pool);
        for (User u : pool) {
            if (candidates.size() == 3) break;
            if (alreadySignaled.contains(u.getId())) continue;

            // 기존 방 있는지 체크
            boolean hasRoom = chatRoomRepository.findByUserAAndUserB(me, u).isPresent()
                    || chatRoomRepository.findByUserBAndUserA(u, me).isPresent();
            if (hasRoom) continue;

            Map<String,Object> row = new LinkedHashMap<>();
            row.put("userId", u.getId());
            row.put("nickname", u.getName());      // 프론트 호환 위해 key는 nickname 유지
            row.put("department", u.getDepartment());
            row.put("age", u.getAge());
            candidates.add(row);
        }

        return MatchResultResponse.builder()
                .ruleHit(0) // 스코어링 폐기
                .candidates(candidates)
                .build();
    }

    /** 신호 보내기(멱등) */
    @Transactional
    public Map<String,Object> sendSignal(UUID meId, UUID targetId){
        if (meId.equals(targetId)) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User target = userRepository.findById(targetId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 같은 학과 금지 + 이성만
        if (Objects.equals(me.getDepartment(), target.getDepartment()))
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getGender() == target.getGender())
            throw new ApiException(ErrorCode.VALIDATION_ERROR);

        // 멱등: 존재하면 그대로 반환
        Signal existing = signalRepository.findBySenderAndReceiver(me, target).orElse(null);
        if (existing == null) {
            existing = signalRepository.save(Signal.builder()
                    .sender(me).receiver(target).status(Signal.Status.SENT).build());
        }

        return Map.of(
                "signalId", existing.getId(),
                "status", existing.getStatus().name()
        );
    }

    /** 내가 보낸 신호 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listSentSignals(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        List<Signal> list = signalRepository.findAllBySenderOrderByCreatedAtDesc(me);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User r = s.getReceiver();
            out.add(Map.of(
                    "signalId", s.getId(),
                    "toUser", Map.of(
                            "userId", r.getId(),
                            "name", r.getName(),
                            "department", r.getDepartment(),
                            "age", r.getAge(),
                            "gender", r.getGender().name()
                    ),
                    "status", s.getStatus().name(),
                    "createdAt", s.getCreatedAt()
            ));
        }
        return out;
    }

    /** 내가 받은 신호 목록(미수락 포함 모두) */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listReceivedSignals(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        List<Signal> list = signalRepository.findAllByReceiverOrderByCreatedAtDesc(me);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User from = s.getSender();
            out.add(Map.of(
                    "signalId", s.getId(),
                    "fromUser", Map.of(
                            "userId", from.getId(),
                            "name", from.getName(),
                            "department", from.getDepartment(),
                            "age", from.getAge(),
                            "gender", from.getGender().name()
                    ),
                    "status", s.getStatus().name(),
                    "createdAt", s.getCreatedAt()
            ));
        }
        return out;
    }

    /** 신호 수락 → MUTUAL로 전환하고 채팅방 생성(멱등) */
    @Transactional
    public Map<String,Object> acceptSignal(UUID meId, UUID signalId){
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!s.getReceiver().getId().equals(meId)) throw new ApiException(ErrorCode.FORBIDDEN);

        // 상태 전환
        s.setStatus(Signal.Status.MUTUAL);
        signalRepository.save(s);

        // 반대 방향 신호도 있으면 같이 MUTUAL로
        signalRepository.findBySenderAndReceiver(s.getReceiver(), s.getSender())
                .ifPresent(other -> {
                    if (other.getStatus() != Signal.Status.MUTUAL) {
                        other.setStatus(Signal.Status.MUTUAL);
                        signalRepository.save(other);
                    }
                });

        // 채팅방 생성/재사용
        ChatRoom room = chatService.createOrReuseRoom(s.getSender().getId(), s.getReceiver().getId());

        return Map.of(
                "roomId", room.getId(),
                "mutual", true
        );
    }

    /** 매칭 성사(서로 수락) 현황 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listMutualMatches(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        List<Map<String,Object>> out = new ArrayList<>();
        // 내가 보낸 MUTUAL
        for (Signal s : signalRepository.findAllBySenderAndStatusOrderByCreatedAtDesc(me, Signal.Status.MUTUAL)) {
            User peer = s.getReceiver();
            UUID roomId = chatRoomIdBetween(me, peer);
            out.add(matchRow(peer, roomId, s.getCreatedAt()));
        }
        // 내가 받은 MUTUAL
        for (Signal s : signalRepository.findAllByReceiverAndStatusOrderByCreatedAtDesc(me, Signal.Status.MUTUAL)) {
            User peer = s.getSender();
            UUID roomId = chatRoomIdBetween(me, peer);
            out.add(matchRow(peer, roomId, s.getCreatedAt()));
        }
        // 중복 제거(동일 상대는 하나만)
        LinkedHashMap<UUID, Map<String,Object>> dedup = new LinkedHashMap<>();
        for (Map<String,Object> row : out) {
            dedup.put((UUID) ((Map<?,?>)row.get("peer")).get("userId"), row);
        }
        return new ArrayList<>(dedup.values());
    }

    private Map<String,Object> matchRow(User peer, UUID roomId, Object matchedAt){
        return Map.of(
                "peer", Map.of(
                        "userId", peer.getId(),
                        "name", peer.getName(),
                        "department", peer.getDepartment(),
                        "age", peer.getAge(),
                        "gender", peer.getGender().name()
                ),
                "roomId", roomId,
                "matchedAt", matchedAt
        );
    }

    private UUID chatRoomIdBetween(User a, User b){
        return chatRoomRepository.findByUserAAndUserB(a, b)
                .or(() -> chatRoomRepository.findByUserBAndUserA(a, b))
                .map(ChatRoom::getId).orElse(null);
    }
}
