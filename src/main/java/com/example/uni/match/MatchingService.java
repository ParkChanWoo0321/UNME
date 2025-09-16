// com/example/uni/match/MatchingService.java
package com.example.uni.match;

import com.example.uni.chat.ChatRoom;
import com.example.uni.chat.ChatRoomRepository;
import com.example.uni.chat.ChatService;
import com.example.uni.common.domain.AfterCommitExecutor;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.common.realtime.RealtimeNotifier;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserCandidateRepository;
import com.example.uni.user.repo.UserRepository;
import com.example.uni.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final AfterCommitExecutor afterCommit;
    private final UserService userService;

    /** 매칭 시작 */
    @Transactional
    public MatchResultResponse requestMatch(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (!me.isProfileComplete() || me.getGender() == null)
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getMatchCredits() < 1)
            throw new ApiException(ErrorCode.MATCH_CREDITS_EXHAUSTED);

        Gender opposite = (me.getGender()==Gender.MALE) ? Gender.FEMALE : Gender.MALE;

        List<User> pool = userCandidateRepository
                .findByGenderAndDepartmentNotAndProfileCompleteTrueAndIdNot(
                        opposite, me.getDepartment(), me.getId()
                );

        Set<Long> alreadySignaled = new HashSet<>();
        signalRepository.findAllBySender(me).forEach(s -> alreadySignaled.add(s.getReceiver().getId()));

        List<Map<String,Object>> candidates = new ArrayList<>();
        Collections.shuffle(pool);
        for (User u : pool) {
            if (candidates.size() == 3) break;
            if (alreadySignaled.contains(u.getId())) continue;

            boolean hasRoom = chatRoomRepository.findByUserAAndUserB(me, u).isPresent()
                    || chatRoomRepository.findByUserBAndUserA(u, me).isPresent();
            if (hasRoom) continue;

            candidates.add(matchCandidateCard(u));
        }

        if (candidates.isEmpty()) {
            return MatchResultResponse.builder().candidates(candidates).build();
        }

        me.setMatchCredits(me.getMatchCredits() - 1);
        return MatchResultResponse.builder().candidates(candidates).build();
    }

    /** 신호 보내기 */
    @Transactional
    public Map<String,Object> sendSignal(Long meId, Long targetId){
        if (Objects.equals(meId, targetId)) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (Objects.equals(me.getDepartment(), target.getDepartment()))
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getGender() == target.getGender())
            throw new ApiException(ErrorCode.VALIDATION_ERROR);

        boolean hasRoom = chatRoomRepository.findByUserAAndUserB(me, target).isPresent()
                || chatRoomRepository.findByUserBAndUserA(target, me).isPresent();
        if (hasRoom) throw new ApiException(ErrorCode.CONFLICT);

        Signal s = signalRepository.findBySenderAndReceiver(me, target).orElse(null);

        if (s == null) {
            if (me.getSignalCredits() < 1) throw new ApiException(ErrorCode.SIGNAL_CREDITS_EXHAUSTED);
            me.setSignalCredits(me.getSignalCredits() - 1);

            s = signalRepository.save(Signal.builder()
                    .sender(me).receiver(target).status(Signal.Status.SENT).build());

            afterCommit.run(() -> notifier.toUser(
                    target.getId(), RealtimeNotifier.Q_SIGNAL,
                    Map.of("type","SENT","fromUser", publicUserCard(me))
            ));
        } else {
            if (s.getStatus() == Signal.Status.MUTUAL) throw new ApiException(ErrorCode.CONFLICT);
            if (s.getStatus() != Signal.Status.SENT) {
                s.setStatus(Signal.Status.SENT);
                s.setReceiverDeletedAt(null); // 재발송 시 수신자 숨김 해제
                signalRepository.save(s);

                afterCommit.run(() -> notifier.toUser(
                        target.getId(), RealtimeNotifier.Q_SIGNAL,
                        Map.of("type","SENT","fromUser", publicUserCard(me))
                ));
            }
        }

        return Map.of("signalId", s.getId(), "status", s.getStatus().name());
    }

    /** 신호 거절(받은 사람) */
    @Transactional
    public Map<String,Object> declineSignal(Long meId, Long signalId){
        Signal s = signalRepository.findById(signalId)
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNAL_NOT_FOUND));
        if (!Objects.equals(s.getReceiver().getId(), meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() != Signal.Status.SENT) throw new ApiException(ErrorCode.CONFLICT);

        s.setStatus(Signal.Status.DECLINED);
        s.setReceiverDeletedAt(LocalDateTime.now()); // 수신자 목록에서 숨김(삭제 효과)
        signalRepository.save(s);

        afterCommit.run(() -> notifier.toUser(
                s.getSender().getId(), RealtimeNotifier.Q_SIGNAL,
                Map.of("type","DECLINED","fromUser", publicUserCard(s.getReceiver()))
        ));
        return Map.of("ok", true);
    }

    /** 신호 수락(받은 사람) — 방 생성/재사용 + 성사 알림 + 신호 삭제 */
    @Transactional
    public Map<String,Object> acceptSignal(Long meId, Long signalId){
        Signal s = signalRepository.findById(signalId)
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNAL_NOT_FOUND));
        if (!Objects.equals(s.getReceiver().getId(), meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() != Signal.Status.SENT) throw new ApiException(ErrorCode.CONFLICT);

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

        afterCommit.run(() -> {
            notifier.toUser(s.getSender().getId(),   RealtimeNotifier.Q_MATCH, forSender);
            notifier.toUser(s.getReceiver().getId(), RealtimeNotifier.Q_MATCH, forReceiver);
        });

        // 성사 후 신호 삭제
        signalRepository.deleteBySenderAndReceiver(s.getSender(), s.getReceiver());
        signalRepository.deleteBySenderAndReceiver(s.getReceiver(), s.getSender());

        String createdAt = null;
        if (room.getCreatedAt() != null) {
            createdAt = room.getCreatedAt()
                    .atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(java.time.ZoneOffset.UTC)
                    .toInstant()
                    .toString();
        }

        Map<String,Object> resp = new LinkedHashMap<>();
        resp.put("roomId", room.getId().toString());
        resp.put("participants", List.of(s.getSender().getId(), s.getReceiver().getId()));
        resp.put("createdAt", createdAt);
        return resp;
    }

    // 신호 목록용 간소 카드(수신목록 전용) — 항상 typeImageUrl2
    private Map<String, Object> signalUserCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("name", u.getName());
        card.put("department", u.getDepartment());
        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        card.put("typeImageUrl2", userService.resolveTypeImage2(typeId));
        return card;
    }

    /** 보낸 신호 목록 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listSentSignals(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Signal> list = signalRepository.findAllBySenderOrderByCreatedAtDesc(me);

        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User r = s.getReceiver();
            int typeId = (r.getTypeId() != null) ? r.getTypeId() : 4;

            // 상태별 이미지 키/메시지
            Map<String,Object> toCard = new LinkedHashMap<>();
            toCard.put("userId", r.getId());
            toCard.put("name", r.getName());
            toCard.put("department", r.getDepartment());
            String message;
            if (s.getStatus() == Signal.Status.DECLINED) {
                toCard.put("typeImageUrl3", userService.resolveTypeImage3(typeId)); // <- 여기서 3로 전환
                message = "거절하셨습니다.";
            } else {
                toCard.put("typeImageUrl2", userService.resolveTypeImage2(typeId));
                message = "성공적으로 신호를 보냈어요!";
            }

            Map<String,Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("toUser", toCard);
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            row.put("message", message);
            out.add(row);
        }
        return out;
    }

    /** 받은 신호 목록 (수신자 숨김 처리 반영) */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listReceivedSignals(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 수신자 입장에서 삭제된 항목은 제외
        List<Signal> list = signalRepository.findAllByReceiverAndReceiverDeletedAtIsNullOrderByCreatedAtDesc(me);

        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            if (s.getStatus() != Signal.Status.SENT) continue; // 받은 목록엔 대기중만 노출
            User from = s.getSender();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("fromUser", signalUserCard(from));
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            row.put("message", "새로운 신호가 있어요!");
            out.add(row);
        }
        return out;
    }

    /** 다른 API에서 쓰는 기본 카드 */
    private Map<String, Object> publicUserCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("name", u.getName());
        card.put("department", u.getDepartment());
        card.put("introduce", u.getIntroduce());

        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        card.put("typeId", typeId);
        card.put("typeImageUrl",  userService.resolveTypeImage(typeId));
        card.put("typeImageUrl2", userService.resolveTypeImage2(typeId));
        return card;
    }

    /** 매칭 후보 전용 카드 (typeId, typeImageUrl2 제외) */
    private Map<String,Object> matchCandidateCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("name", u.getName());
        card.put("department", u.getDepartment());
        card.put("introduce", u.getIntroduce());

        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        card.put("typeImageUrl", userService.resolveTypeImage(typeId));
        return card;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMutualMatches(Long meId) {
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        var rooms = chatRoomRepository.findAllForUserOrderByUpdatedAtDesc(me);

        List<Map<String,Object>> out = new ArrayList<>();
        for (ChatRoom r : rooms) {
            User peer = r.getUserA().getId().equals(me.getId()) ? r.getUserB() : r.getUserA();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("peer", publicUserCard(peer));
            row.put("roomId", r.getId());
            row.put("matchedAt", r.getCreatedAt());
            out.add(row);
        }
        return out;
    }
}
