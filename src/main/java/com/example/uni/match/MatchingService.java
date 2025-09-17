// com/example/uni/match/MatchingService.java
package com.example.uni.match;

import com.example.uni.chat.ChatRoomService;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final ChatRoomService chatRoomService;   // Firestore 방 서비스
    private final RealtimeNotifier notifier;
    private final AfterCommitExecutor afterCommit;
    private final UserService userService;

    @Value("${app.unknown-user.name:알 수 없는 유저}")
    private String unknownUserName;
    @Value("${app.unknown-user.image:}")
    private String unknownUserImage;

    /** 매칭 시작 */
    @Transactional
    public MatchResultResponse requestMatch(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (me.getDeactivatedAt()!=null) throw new ApiException(ErrorCode.FORBIDDEN);
        if (!me.isProfileComplete() || me.getGender() == null)
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getMatchCredits() < 1)
            throw new ApiException(ErrorCode.MATCH_CREDITS_EXHAUSTED);

        Gender opposite = (me.getGender()==Gender.MALE) ? Gender.FEMALE : Gender.MALE;

        var pool = userCandidateRepository
                .findByGenderAndDepartmentNotAndProfileCompleteTrueAndDeactivatedAtIsNullAndIdNot(
                        opposite, me.getDepartment(), me.getId()
                );

        Set<Long> alreadySignaled = new HashSet<>();
        signalRepository.findAllBySender(me).forEach(s -> alreadySignaled.add(s.getReceiver().getId()));

        List<Map<String,Object>> candidates = new ArrayList<>();
        Collections.shuffle(pool);
        for (User u : pool) {
            if (candidates.size() == 3) break;
            if (alreadySignaled.contains(u.getId())) continue;

            boolean hasRoom = chatRoomService.existsBetween(me.getId(), u.getId());
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

        if (me.getDeactivatedAt()!=null || target.getDeactivatedAt()!=null)
            throw new ApiException(ErrorCode.FORBIDDEN);

        if (Objects.equals(me.getDepartment(), target.getDepartment()))
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getGender() == target.getGender())
            throw new ApiException(ErrorCode.VALIDATION_ERROR);

        if (chatRoomService.existsBetween(me.getId(), target.getId()))
            throw new ApiException(ErrorCode.CONFLICT);

        Signal s = signalRepository.findBySenderAndReceiver(me, target).orElse(null);

        if (s == null) {
            if (me.getSignalCredits() < 1) throw new ApiException(ErrorCode.SIGNAL_CREDITS_EXHAUSTED);
            me.setSignalCredits(me.getSignalCredits() - 1);

            s = signalRepository.save(Signal.builder()
                    .sender(me).receiver(target).status(Signal.Status.SENT).build());

            // ★ 목록(listReceived) 스키마와 동일하게 푸시
            final String message = "새로운 신호가 있어요!";
            final Map<String,Object> payload = new LinkedHashMap<>();
            payload.put("type", "SENT");
            payload.put("signalId", s.getId());
            payload.put("status", s.getStatus().name());
            payload.put("createdAt", s.getCreatedAt());
            payload.put("message", message);
            payload.put("fromUser", signalUserCard(me)); // listReceived와 동일 카드

            afterCommit.run(() -> notifier.toUser(target.getId(), RealtimeNotifier.Q_SIGNAL, payload));
        } else {
            if (s.getStatus() == Signal.Status.MUTUAL) throw new ApiException(ErrorCode.CONFLICT);
            if (s.getStatus() != Signal.Status.SENT) {
                s.setStatus(Signal.Status.SENT);
                s.setReceiverDeletedAt(null);
                signalRepository.save(s);

                // ★ 동일 스키마
                final String message = "새로운 신호가 있어요!";
                final Map<String,Object> payload = new LinkedHashMap<>();
                payload.put("type", "SENT");
                payload.put("signalId", s.getId());
                payload.put("status", s.getStatus().name());
                payload.put("createdAt", s.getCreatedAt());
                payload.put("message", message);
                payload.put("fromUser", signalUserCard(me));

                afterCommit.run(() -> notifier.toUser(target.getId(), RealtimeNotifier.Q_SIGNAL, payload));
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
        s.setReceiverDeletedAt(LocalDateTime.now());
        signalRepository.save(s);

        // ★ 목록(listSent) 스키마와 동일하게 푸시 (toUser + status + message + createdAt)
        User r = s.getReceiver(); // 거절한 사람(보낸 사람의 toUser가 됨)
        boolean deactivated = (r.getDeactivatedAt()!=null);
        int typeId = (r.getTypeId() != null) ? r.getTypeId() : 4;

        Map<String,Object> toCard = new LinkedHashMap<>();
        toCard.put("userId", r.getId());
        toCard.put("name", deactivated ? unknownUserName : r.getName());
        toCard.put("department", deactivated ? null : r.getDepartment());
        if (deactivated) {
            toCard.put("typeImageUrl2", unknownUserImage);
            toCard.put("typeImageUrl3", unknownUserImage);
        } else {
            toCard.put("typeImageUrl3", userService.resolveTypeImage3(typeId)); // DECLINED일 땐 3번 이미지
        }

        final Map<String,Object> payload = new LinkedHashMap<>();
        payload.put("type", "DECLINED");
        payload.put("signalId", s.getId());
        payload.put("status", s.getStatus().name());           // "DECLINED"
        payload.put("createdAt", s.getCreatedAt());
        payload.put("message", "거절하셨습니다.");
        payload.put("toUser", toCard);                          // listSent와 동일 키/카드

        afterCommit.run(() -> notifier.toUser(
                s.getSender().getId(), RealtimeNotifier.Q_SIGNAL, payload
        ));
        return Map.of("ok", true);
    }

    /** 신호 수락(받은 사람) — Firestore 방 생성 + 성사 알림 + 신호 삭제 */
    @Transactional
    public Map<String,Object> acceptSignal(Long meId, Long signalId){
        Signal s = signalRepository.findById(signalId)
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNAL_NOT_FOUND));
        if (!Objects.equals(s.getReceiver().getId(), meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() != Signal.Status.SENT) throw new ApiException(ErrorCode.CONFLICT);

        if (s.getSender().getDeactivatedAt()!=null || s.getReceiver().getDeactivatedAt()!=null)
            throw new ApiException(ErrorCode.FORBIDDEN);

        s.setStatus(Signal.Status.MUTUAL);
        signalRepository.save(s);

        signalRepository.findBySenderAndReceiver(s.getReceiver(), s.getSender())
                .ifPresent(other -> {
                    if (other.getStatus() != Signal.Status.MUTUAL) {
                        other.setStatus(Signal.Status.MUTUAL);
                        signalRepository.save(other);
                    }
                });

        // peers 맵 구성: 키=각 사용자ID, 값=상대 요약카드
        Map<String,Object> peers = new LinkedHashMap<>();
        peers.put(String.valueOf(s.getSender().getId()),   peerBrief(s.getReceiver()));
        peers.put(String.valueOf(s.getReceiver().getId()), peerBrief(s.getSender()));

        // Firestore에 채팅방 생성
        Map<String,Object> resp = chatRoomService.openRoom(
                List.of(s.getSender().getId(), s.getReceiver().getId()),
                peers
        ); // { roomId, participants, peers, createdAt }

        // 성사 알림(목록에서 해당 상대 항목 제거할 수 있도록 peerUserId 제공)
        String roomId = String.valueOf(resp.get("roomId"));
        Map<String,Object> forSender   = new LinkedHashMap<>();
        forSender.put("type","MUTUAL");
        forSender.put("roomId", roomId);
        forSender.put("peer", publicUserCard(s.getReceiver()));
        forSender.put("peerUserId", s.getReceiver().getId()); // ← 목록 제거용 힌트

        Map<String,Object> forReceiver = new LinkedHashMap<>();
        forReceiver.put("type","MUTUAL");
        forReceiver.put("roomId", roomId);
        forReceiver.put("peer", publicUserCard(s.getSender()));
        forReceiver.put("peerUserId", s.getSender().getId()); // ← 목록 제거용 힌트

        afterCommit.run(() -> {
            notifier.toUser(s.getSender().getId(),   RealtimeNotifier.Q_MATCH, forSender);
            notifier.toUser(s.getReceiver().getId(), RealtimeNotifier.Q_MATCH, forReceiver);
        });

        // 성사 후 신호 삭제(양방향)
        signalRepository.deleteBySenderAndReceiver(s.getSender(), s.getReceiver());
        signalRepository.deleteBySenderAndReceiver(s.getReceiver(), s.getSender());

        return resp;
    }

    // 받은목록 카드: 탈퇴자면 마스킹
    private Map<String, Object> signalUserCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());

        boolean deactivated = (u.getDeactivatedAt()!=null);
        card.put("name",       deactivated ? unknownUserName  : u.getName());
        card.put("department", deactivated ? null             : u.getDepartment());

        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        card.put("typeImageUrl2", deactivated ? unknownUserImage : userService.resolveTypeImage2(typeId));
        return card;
    }

    /** 받은 사람이 보게 될 상대 요약 카드 */
    private Map<String, Object> peerBrief(User peer) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId", peer.getId());
        m.put("name", peer.getName());
        m.put("department", peer.getDepartment());
        int typeId = (peer.getTypeId() != null) ? peer.getTypeId() : 4;
        m.put("typeImageUrl", userService.resolveTypeImage(typeId));
        return m;
    }

    /** 보낸 신호 목록 — 탈퇴자 메시지 반영 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listSentSignals(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Signal> list = signalRepository.findAllBySenderOrderByCreatedAtDesc(me);

        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User r = s.getReceiver();
            boolean deactivated = (r.getDeactivatedAt()!=null);
            int typeId = (r.getTypeId() != null) ? r.getTypeId() : 4;

            Map<String,Object> toCard = new LinkedHashMap<>();
            toCard.put("userId", r.getId());
            toCard.put("name", deactivated ? unknownUserName : r.getName());
            toCard.put("department", deactivated ? null : r.getDepartment());

            String message;
            if (deactivated) {
                toCard.put("typeImageUrl2", unknownUserImage);
                toCard.put("typeImageUrl3", unknownUserImage);
                message = "탈퇴한 사용자입니다.";
            } else if (s.getStatus() == Signal.Status.DECLINED) {
                toCard.put("typeImageUrl3", userService.resolveTypeImage3(typeId));
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

    /** 받은 신호 목록 — 탈퇴자 메시지 반영 */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> listReceivedSignals(Long meId){
        User me = userRepository.findById(meId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Signal> list = signalRepository.findAllByReceiverAndReceiverDeletedAtIsNullOrderByCreatedAtDesc(me);

        List<Map<String,Object>> out = new ArrayList<>();
        for (Signal s : list) {
            if (s.getStatus() != Signal.Status.SENT) continue;
            User from = s.getSender();
            boolean deactivated = (from.getDeactivatedAt()!=null);

            Map<String,Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("fromUser", signalUserCard(from)); // 마스킹 적용됨
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            row.put("message", deactivated ? "탈퇴한 사용자입니다." : "새로운 신호가 있어요!");
            out.add(row);
        }
        return out;
    }

    /** 다른 API에서 쓰는 기본 카드 — 탈퇴자 마스킹 */
    private Map<String, Object> publicUserCard(User u) {
        Map<String,Object> card = new LinkedHashMap<>();
        boolean deactivated = (u.getDeactivatedAt()!=null);

        card.put("userId", u.getId());
        card.put("name", deactivated ? unknownUserName : u.getName());
        card.put("department", deactivated ? null : u.getDepartment());
        card.put("introduce", deactivated ? null : u.getIntroduce());

        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        card.put("typeId", typeId);
        card.put("typeImageUrl",  deactivated ? unknownUserImage : userService.resolveTypeImage(typeId));
        card.put("typeImageUrl2", deactivated ? unknownUserImage : userService.resolveTypeImage2(typeId));
        return card;
    }

    /** 매칭 후보 전용 카드 */
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
}
