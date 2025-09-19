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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final SignalLogRepository signalLogRepository;
    private final ChatRoomService chatRoomService;
    private final RealtimeNotifier notifier;
    private final AfterCommitExecutor afterCommit;
    private final UserService userService;
    private final ObjectMapper om;

    @Value("${app.unknown-user.name:알 수 없는 유저}")
    private String unknownUserName;
    @Value("${app.unknown-user.image:}")
    private String unknownUserImage;

    @Transactional(readOnly = true)
    public Map<String, Object> previousMatches(Long meId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            if (me.getLastMatchJson() != null && !me.getLastMatchJson().isBlank()) {
                list = om.readValue(me.getLastMatchJson(), new TypeReference<>() {});
            }
        } catch (Exception ignore) {}
        for (Map<String, Object> c : list) addClientAliases(c);
        return Map.of("candidates", list);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> signalStatus(Long meId, Long targetId) {
        if (Objects.equals(meId, targetId)) return Map.of("alreadySent", false);
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        boolean already = signalRepository.findBySenderAndReceiver(me, target).isPresent();
        return Map.of("alreadySent", already);
    }

    @Transactional
    public MatchResultResponse requestMatch(Long meId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (me.getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        if (!me.isProfileComplete() || me.getGender() == null) throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getMatchCredits() < 1) throw new ApiException(ErrorCode.MATCH_CREDITS_EXHAUSTED);

        Gender opposite = (me.getGender() == Gender.MALE) ? Gender.FEMALE : Gender.MALE;
        var pool = userCandidateRepository.findCandidates(opposite, me.getDepartment(), me.getId());

        Set<Long> alreadySignaled = new HashSet<>();
        signalRepository.findAllBySender(me).forEach(s -> alreadySignaled.add(s.getReceiver().getId()));

        List<Map<String, Object>> candidates = new ArrayList<>();
        Collections.shuffle(pool);
        for (User u : pool) {
            if (candidates.size() == 3) break;
            if (alreadySignaled.contains(u.getId())) continue;
            boolean hasRoom = chatRoomService.existsBetween(me.getId(), u.getId());
            if (hasRoom) continue;
            Map<String, Object> card = matchCandidateCard(u);
            addClientAliases(card);
            candidates.add(card);
        }

        if (candidates.isEmpty()) {
            return MatchResultResponse.builder().candidates(candidates).build();
        }

        me.setMatchCredits(me.getMatchCredits() - 1);
        try {
            me.setLastMatchJson(om.writeValueAsString(candidates));
        } catch (Exception e) {
            me.setLastMatchJson("[]");
        }
        me.setLastMatchAt(LocalDateTime.now());
        userRepository.save(me);

        return MatchResultResponse.builder().candidates(candidates).build();
    }

    @Transactional
    public Map<String, Object> sendSignal(Long meId, Long targetId) {
        if (Objects.equals(meId, targetId)) throw new ApiException(ErrorCode.VALIDATION_ERROR);
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (me.getDeactivatedAt() != null || target.getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        if (Objects.equals(me.getDepartment(), target.getDepartment())) throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (me.getGender() == target.getGender()) throw new ApiException(ErrorCode.VALIDATION_ERROR);
        if (chatRoomService.existsBetween(me.getId(), target.getId())) throw new ApiException(ErrorCode.CONFLICT);

        Signal s = signalRepository.findBySenderAndReceiver(me, target).orElse(null);
        if (s == null) {
            if (me.getSignalCredits() < 1) throw new ApiException(ErrorCode.SIGNAL_CREDITS_EXHAUSTED);
            me.setSignalCredits(me.getSignalCredits() - 1);
            s = signalRepository.save(Signal.builder().sender(me).receiver(target).status(Signal.Status.SENT).build());
            signalLogRepository.save(SignalLog.builder()
                    .senderId(me.getId())
                    .receiverId(target.getId())
                    .receiverDepartment(target.getDepartment())
                    .build());
            String message = "새로운 신호가 있어요!";
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("type", "SENT");
            payload.put("signalId", s.getId());
            payload.put("status", s.getStatus().name());
            payload.put("createdAt", s.getCreatedAt());
            payload.put("message", message);
            payload.put("fromUser", signalUserCard(me));
            afterCommit.run(() -> notifier.toUser(target.getId(), RealtimeNotifier.Q_SIGNAL, payload));
        } else {
            if (s.getStatus() == Signal.Status.MUTUAL) throw new ApiException(ErrorCode.CONFLICT);
            if (s.getStatus() != Signal.Status.SENT) {
                s.setStatus(Signal.Status.SENT);
                s.setReceiverDeletedAt(null);
                signalRepository.save(s);
                signalLogRepository.save(SignalLog.builder()
                        .senderId(me.getId())
                        .receiverId(target.getId())
                        .receiverDepartment(target.getDepartment())
                        .build());
                String message = "새로운 신호가 있어요!";
                Map<String, Object> payload = new LinkedHashMap<>();
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

    @Transactional
    public Map<String, Object> declineSignal(Long meId, Long signalId) {
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.SIGNAL_NOT_FOUND));
        if (!Objects.equals(s.getReceiver().getId(), meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() != Signal.Status.SENT) throw new ApiException(ErrorCode.CONFLICT);
        s.setStatus(Signal.Status.DECLINED);
        s.setReceiverDeletedAt(LocalDateTime.now());
        signalRepository.save(s);
        User r = s.getReceiver();
        boolean deactivated = (r.getDeactivatedAt() != null);
        int typeId = (r.getTypeId() != null) ? r.getTypeId() : 4;
        Map<String, Object> toCard = new LinkedHashMap<>();
        toCard.put("userId", r.getId());
        toCard.put("name", deactivated ? unknownUserName : r.getName());
        toCard.put("department", deactivated ? null : r.getDepartment());
        if (deactivated) {
            toCard.put("typeImageUrl2", unknownUserImage);
            toCard.put("typeImageUrl3", unknownUserImage);
        } else {
            String profile = r.getProfileImageUrl();
            String img3 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage3(typeId);
            toCard.put("typeImageUrl3", img3);
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "DECLINED");
        payload.put("signalId", s.getId());
        payload.put("status", s.getStatus().name());
        payload.put("createdAt", s.getCreatedAt());
        payload.put("message", "거절하셨습니다.");
        payload.put("toUser", toCard);
        afterCommit.run(() -> notifier.toUser(s.getSender().getId(), RealtimeNotifier.Q_SIGNAL, payload));
        return Map.of("ok", true);
    }

    @Transactional
    public Map<String, Object> acceptSignal(Long meId, Long signalId) {
        Signal s = signalRepository.findById(signalId).orElseThrow(() -> new ApiException(ErrorCode.SIGNAL_NOT_FOUND));
        if (!Objects.equals(s.getReceiver().getId(), meId)) throw new ApiException(ErrorCode.FORBIDDEN);
        if (s.getStatus() != Signal.Status.SENT) throw new ApiException(ErrorCode.CONFLICT);
        if (s.getSender().getDeactivatedAt() != null || s.getReceiver().getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        s.setStatus(Signal.Status.MUTUAL);
        signalRepository.save(s);
        signalRepository.findBySenderAndReceiver(s.getReceiver(), s.getSender()).ifPresent(other -> {
            if (other.getStatus() != Signal.Status.MUTUAL) {
                other.setStatus(Signal.Status.MUTUAL);
                signalRepository.save(other);
            }
        });
        Map<String, Object> peers = new LinkedHashMap<>();
        peers.put(String.valueOf(s.getSender().getId()), peerBrief(s.getReceiver()));
        peers.put(String.valueOf(s.getReceiver().getId()), peerBrief(s.getSender()));
        Map<String, Object> resp = chatRoomService.openRoom(List.of(s.getSender().getId(), s.getReceiver().getId()), peers);
        String roomId = String.valueOf(resp.get("roomId"));
        Map<String, Object> forSender = new LinkedHashMap<>();
        forSender.put("type", "MUTUAL");
        forSender.put("roomId", roomId);
        forSender.put("peer", publicUserCard(s.getReceiver()));
        forSender.put("peerUserId", s.getReceiver().getId());
        Map<String, Object> forReceiver = new LinkedHashMap<>();
        forReceiver.put("type", "MUTUAL");
        forReceiver.put("roomId", roomId);
        forReceiver.put("peer", publicUserCard(s.getSender()));
        forReceiver.put("peerUserId", s.getSender().getId());
        afterCommit.run(() -> {
            notifier.toUser(s.getSender().getId(), RealtimeNotifier.Q_MATCH, forSender);
            notifier.toUser(s.getReceiver().getId(), RealtimeNotifier.Q_MATCH, forReceiver);
        });
        signalRepository.deleteBySenderAndReceiver(s.getSender(), s.getReceiver());
        signalRepository.deleteBySenderAndReceiver(s.getReceiver(), s.getSender());
        return resp;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listSentSignals(Long meId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Signal> list = signalRepository.findAllBySenderOrderByCreatedAtDesc(me);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Signal s : list) {
            User r = s.getReceiver();
            boolean deactivated = (r.getDeactivatedAt() != null);
            int typeId = (r.getTypeId() != null) ? r.getTypeId() : 4;
            Map<String, Object> toCard = new LinkedHashMap<>();
            toCard.put("userId", r.getId());
            toCard.put("name", deactivated ? unknownUserName : r.getName());
            toCard.put("department", deactivated ? null : r.getDepartment());
            String message;
            if (deactivated) {
                toCard.put("typeImageUrl2", unknownUserImage);
                toCard.put("typeImageUrl3", unknownUserImage);
                message = "탈퇴한 사용자입니다.";
            } else if (s.getStatus() == Signal.Status.DECLINED) {
                String profile = r.getProfileImageUrl();
                String img3 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage3(typeId);
                toCard.put("typeImageUrl3", img3);
                message = "거절하셨습니다.";
            } else {
                String profile = r.getProfileImageUrl();
                String img2 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage2(typeId);
                toCard.put("typeImageUrl2", img2);
                message = "성공적으로 신호를 보냈어요!";
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("toUser", toCard);
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            row.put("message", message);
            out.add(row);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listReceivedSignals(Long meId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Signal> list = signalRepository.findAllByReceiverAndReceiverDeletedAtIsNullOrderByCreatedAtDesc(me);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Signal s : list) {
            if (s.getStatus() != Signal.Status.SENT) continue;
            User from = s.getSender();
            boolean deactivated = (from.getDeactivatedAt() != null);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("signalId", s.getId());
            row.put("fromUser", signalUserCard(from));
            row.put("status", s.getStatus().name());
            row.put("createdAt", s.getCreatedAt());
            row.put("message", deactivated ? "탈퇴한 사용자입니다." : "새로운 신호가 있어요!");
            out.add(row);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> rankDepartments(int limit) {
        List<Object[]> rows = signalLogRepository.countByReceiverDepartment();
        List<Map<String, Object>> out = new ArrayList<>();
        int rank = 1;
        for (Object[] r : rows) {
            if (out.size() >= limit) break;
            String dept = (String) r[0];
            long cnt = (r[1] instanceof Long) ? (Long) r[1] : ((Number) r[1]).longValue();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("rank", rank++);
            m.put("department", dept);
            m.put("count", cnt);
            out.add(m);
        }
        return out;
    }

    private Map<String, Object> signalUserCard(User u) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        boolean deactivated = (u.getDeactivatedAt() != null);
        card.put("name", deactivated ? unknownUserName : u.getName());
        card.put("department", deactivated ? null : u.getDepartment());
        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        if (deactivated) {
            card.put("typeImageUrl2", unknownUserImage);
        } else {
            String profile = u.getProfileImageUrl();
            String img2 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage2(typeId);
            card.put("typeImageUrl2", img2);
        }
        card.put("id", u.getId());
        card.put("targetUserId", u.getId());
        card.put("nickname", deactivated ? null : u.getName());
        card.put("major", deactivated ? null : u.getDepartment());
        String avatar = (u.getProfileImageUrl() != null && !u.getProfileImageUrl().trim().isEmpty()) ? u.getProfileImageUrl() : null;
        card.put("avatarUrl", avatar);
        card.put("profileImageUrl", avatar);
        return card;
    }

    private Map<String, Object> peerBrief(User peer) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId", peer.getId());
        m.put("name", peer.getName());
        m.put("department", peer.getDepartment());
        int typeId = (peer.getTypeId() != null) ? peer.getTypeId() : 4;
        String profile = peer.getProfileImageUrl();
        String img2 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage2(typeId);
        m.put("typeImageUrl2", img2);
        return m;
    }

    private List<Map<String, Object>> aliasList(List<Map<String, Object>> list) {
        for (Map<String, Object> c : list) addClientAliases(c);
        return list;
    }

    private Map<String, Object> publicUserCard(User u) {
        Map<String, Object> card = new LinkedHashMap<>();
        boolean deactivated = (u.getDeactivatedAt() != null);
        card.put("userId", u.getId());
        card.put("name", deactivated ? unknownUserName : u.getName());
        card.put("department", deactivated ? null : u.getDepartment());
        card.put("introduce", deactivated ? null : u.getIntroduce());
        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        if (deactivated) {
            card.put("typeId", typeId);
            card.put("typeImageUrl", unknownUserImage);
            card.put("typeImageUrl2", unknownUserImage);
        } else {
            String profile = u.getProfileImageUrl();
            String img1 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage(typeId);
            String img2 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage2(typeId);
            card.put("typeId", typeId);
            card.put("typeImageUrl", img1);
            card.put("typeImageUrl2", img2);
        }
        return card;
    }

    private Map<String, Object> matchCandidateCard(User u) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("userId", u.getId());
        card.put("name", u.getName());
        card.put("department", u.getDepartment());
        card.put("introduce", u.getIntroduce());
        int typeId = (u.getTypeId() != null) ? u.getTypeId() : 4;
        String profile = u.getProfileImageUrl();
        String img1 = (profile != null && !profile.trim().isEmpty()) ? profile : userService.resolveTypeImage(typeId);
        card.put("typeImageUrl", img1);
        card.put("id", u.getId());
        card.put("targetUserId", u.getId());
        card.put("nickname", u.getName() != null ? u.getName() : u.getNickname());
        card.put("major", u.getDepartment());
        String avatar = (profile != null && !profile.trim().isEmpty()) ? profile : null;
        card.put("avatarUrl", avatar);
        card.put("profileImageUrl", avatar);
        return card;
    }

    private void addClientAliases(Map<String, Object> card) {
        Object uid = card.getOrDefault("userId", card.get("id"));
        if (uid != null) {
            card.putIfAbsent("userId", uid);
            card.putIfAbsent("id", uid);
            card.putIfAbsent("targetUserId", uid);
        }
        Object name = card.get("name");
        if (name != null) card.putIfAbsent("nickname", name);
        Object dept = card.get("department");
        if (dept != null) card.putIfAbsent("major", dept);
        Object avatar = card.get("avatarUrl");
        if (avatar == null) {
            avatar = card.get("profileImageUrl");
            if (avatar != null) card.putIfAbsent("avatarUrl", avatar);
        } else {
            card.putIfAbsent("profileImageUrl", avatar);
        }
    }
}
