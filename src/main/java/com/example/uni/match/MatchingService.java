package com.example.uni.match;

import com.example.uni.chat.ChatRoom;
import com.example.uni.chat.ChatRoomRepository;
import com.example.uni.chat.ChatService;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.common.realtime.RealtimeNotifier;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserCandidateRepository;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final UserCandidateRepository userCandidateRepository;
    private final SignalRepository signalRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final RealtimeNotifier notifier;

    /** 매칭 시작: 이성/다른 학과/본인 제외/기보낸신호 제외/기존채팅 없음 → 랜덤 3명 (크레딧 1 차감) */
    @Transactional
    public MatchResultResponse requestMatch(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        if (!me.isProfileComplete() || me.getGender() == null)
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getMatchCredits() < 1)
            throw new ApiException(ErrorCode.QUOTA_EXCEEDED);

        me.setMatchCredits(me.getMatchCredits() - 1);

        Gender opposite = (me.getGender()==Gender.MALE) ? Gender.FEMALE : Gender.MALE;

        List<User> pool = userCandidateRepository
                .findByGenderAndDepartmentNotAndProfileCompleteTrueAndIdNot(
                        opposite, me.getDepartment(), me.getId()
                );
        Set<UUID> alreadySignaled = new HashSet<>();
        signalRepository.findAllBySender(me).forEach(s -> alreadySignaled.add(s.getReceiver().getId()));
        List<Map<String,Object>> candidates = new ArrayList<>();
        Collections.shuffle(pool);
        for (User u : pool) {
            if (candidates.size() == 3) break;
            if (alreadySignaled.contains(u.getId())) continue;
            boolean hasRoom = chatRoomRepository.findByUserAAndUserB(me, u).isPresent()
                    || chatRoomRepository.findByUserBAndUserA(u, me).isPresent();
            if (hasRoom) continue;
            candidates.add(publicUserCard(u));
        }
        return MatchResultResponse.builder()
                .ruleHit(0)
                .candidates(candidates)
                .build();
    }

    /** 신호 보내기(멱등) + 실시간 알림 */
    @Transactional
    public Map<String,Object> sendSignal(UUID meId, UUID targetId){
        if (meId.equals(targetId)) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User target = userRepository.findById(targetId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        if (Objects.equals(me.getDepartment(), target.getDepartment()))
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getGender() == target.getGender())
            throw new ApiException(ErrorCode.VALIDATION_ERROR);

        boolean hasRoom = chatRoomRepository.findByUserAAndUserB(me, target).isPresent()
                || chatRoomRepository.findByUserBAndUserA(target, me).isPresent();
        if (hasRoom) throw new ApiException(ErrorCode.CONFLICT);

        Signal existing = signalRepository.findBySenderAndReceiver(me, target).orElse(null);
        if (existing == null) {
            existing = signalRepository.save(Signal.builder()
                    .sender(me).receiver(target).status(Signal.Status.SENT).build());

            notifier.toUser(
                    target.getId(),
                    RealtimeNotifier.Q_SIGNAL,
                    Map.of("type","SENT","fromUser", publicUserCard(me))
            );
        }

        return Map.of("signalId", existing.getId(), "status", existing.getStatus().name());
    }

    /** 신호 취소(보낸 사람) */
    @Transactional
    public Map<String,Object> cancelSignal(UUID meId, UUID signalId){
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!s.getSender().getId().equals(meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() == Signal.Status.MUTUAL) throw new ApiException(ErrorCode.CONFLICT);
        s.setStatus(Signal.Status.CANCELED);
        signalRepository.save(s);
        notifier.toUser(s.getReceiver().getId(), RealtimeNotifier.Q_SIGNAL,
                Map.of("type","CANCELED","fromUser", publicUserCard(s.getSender())));
        return Map.of("ok", true);
    }

    /** 신호 거절(받은 사람) */
    @Transactional
    public Map<String,Object> declineSignal(UUID meId, UUID signalId){
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!s.getReceiver().getId().equals(meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() == Signal.Status.MUTUAL) throw new ApiException(ErrorCode.CONFLICT);
        s.setStatus(Signal.Status.DECLINED);
        signalRepository.save(s);
        notifier.toUser(s.getSender().getId(), RealtimeNotifier.Q_SIGNAL,
                Map.of("type","DECLINED","fromUser", publicUserCard(s.getReceiver())));
        return Map.of("ok", true);
    }

    /** 신호 수락 → MUTUAL/채팅방 생성 + 실시간 알림 */
    @Transactional
    public Map<String,Object> acceptSignal(UUID meId, UUID signalId){
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!s.getReceiver().getId().equals(meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        s.setStatus(Signal.Status.MUTUAL);
        signalRepository.save(s);
        signalRepository.findBySenderAndReceiver(s.getReceiver(), s.getSender())
                .ifPresent(other -> {
                    if (other.getStatus() != Signal.Status.MUTUAL) {
                        other.setStatus(Signal.Status.MUTUAL);
                        signalRepository.save(other);
                    }
                });

        ChatRoom room = chatService.createOrReuseRoom(s.getSender().getId(), s.getReceiver().getId());

        Map<String,Object> forSender   = Map.of("type","MUTUAL","roomId", room.getId(), "peer", publicUserCard(s.getReceiver()));
        Map<String,Object> forReceiver = Map.of("type","MUTUAL","roomId", room.getId(), "peer", publicUserCard(s.getSender()));
        notifier.toUser(s.getSender().getId(),   RealtimeNotifier.Q_MATCH, forSender);
        notifier.toUser(s.getReceiver().getId(), RealtimeNotifier.Q_MATCH, forReceiver);

        return Map.of("roomId", room.getId(), "mutual", true);
    }

    /** 내가 보낸 신호 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listSentSignals(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        List<Signal> list = signalRepository.findAllBySenderOrderByCreatedAtDesc(me);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User r = s.getReceiver();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("toUser", publicUserCard(r));
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            out.add(row);
        }
        return out;
    }

    /** 내가 받은 신호 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listReceivedSignals(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        List<Signal> list = signalRepository.findAllByReceiverOrderByCreatedAtDesc(me);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User from = s.getSender();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("fromUser", publicUserCard(from));
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            out.add(row);
        }
        return out;
    }

    /** 매칭 성사 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listMutualMatches(UUID meId){
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        var rooms = chatRoomRepository.findAllForUserOrderByUpdatedAtDesc(me);
        List<Map<String,Object>> out = new ArrayList<>();
        for (ChatRoom r : rooms) {
            User peer = r.getUserA().getId().equals(me.getId()) ? r.getUserB() : r.getUserA();
            out.add(matchRow(peer, r.getId(), r.getCreatedAt()));
        }
        return out;
    }

    /** 후보/신호/매칭 공통 공개 카드 (학과/학번/나이/프로필사진/성향요약) */
    private Map<String, Object> publicUserCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("department", u.getDepartment());
        card.put("studentNo", u.getStudentNo());

        Integer age = (u.getBirthYear() == null) ? null : (Year.now().getValue() - u.getBirthYear());
        if (age != null) card.put("age", age);

        String img = u.getProfileImageUrl();
        if (img != null && !img.isBlank()) card.put("profileImageUrl", img);

        if (u.getTraitsJson() != null && !u.getTraitsJson().isBlank()) {
            card.put("traits", u.getTraitsJson());
        }
        return card;
    }

    private Map<String,Object> matchRow(User peer, UUID roomId, Object matchedAt){
        Map<String,Object> row = new LinkedHashMap<>();
        row.put("peer", publicUserCard(peer));
        row.put("roomId", roomId);
        row.put("matchedAt", matchedAt);
        return row;
    }
}