package com.example.uni.chat;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Firestore firestore;

    private static String roomIdOf(Long a, Long b) {
        long x = Math.min(a, b);
        long y = Math.max(a, b);
        return "r_" + x + "_" + y;
    }

    public boolean existsBetween(Long a, Long b) {
        try {
            String roomId = roomIdOf(a, b);
            DocumentReference ref = firestore.collection("chatRooms").document(roomId);
            return ref.get().get().exists();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * participants: ["123","456"]  // 문자열 UID 배열
     * peers: { "123": {...상대정보}, "456": {...상대정보} }  // "내 UID" 키 아래에 "상대 정보"
     * listCard: { "123": {...상대정보}, "456": {...상대정보} } // 채팅목록용 미러
     */
    public Map<String, Object> openRoom(List<Long> participants, Map<String, Object> peers) {
        if (participants == null || participants.size() != 2)
            throw new IllegalArgumentException("participants must have exactly 2 user IDs");

        Long a = participants.get(0);
        Long b = participants.get(1);
        String roomId = roomIdOf(a, b);
        DocumentReference ref = firestore.collection("chatRooms").document(roomId);

        try {
            DocumentSnapshot snap = ref.get().get();
            if (!snap.exists()) {
                List<Long> sortedNum = new ArrayList<>(participants);
                Collections.sort(sortedNum);
                List<String> participantsStr = List.of(
                        String.valueOf(sortedNum.get(0)),
                        String.valueOf(sortedNum.get(1))
                );

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("participants", participantsStr);
                data.put("peers", peers != null ? peers : Map.of());
                data.put("pairKey", participantsStr.get(0) + "_" + participantsStr.get(1));
                data.put("createdAt", FieldValue.serverTimestamp());
                ref.set(data, SetOptions.merge()).get();
                snap = ref.get().get();
            }

            Timestamp ts = snap.getTimestamp("createdAt");
            String createdAtIso = (ts != null)
                    ? Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()).toString()
                    : null;

            @SuppressWarnings("unchecked")
            Map<String, Object> peersOut = (Map<String, Object>) snap.get("peers");
            if (peersOut == null) peersOut = (peers != null ? peers : Map.of());

            boolean patched = false;
            for (Map.Entry<String, Object> e : new ArrayList<>(peersOut.entrySet())) {
                if (!(e.getValue() instanceof Map)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> p = (Map<String, Object>) e.getValue();

                String avatar = (p.get("avatarUrl") instanceof String s) ? s : null;
                String prof   = (p.get("profileImageUrl") instanceof String s) ? s : null;
                String t2     = (p.get("typeImageUrl2") instanceof String s) ? s : null;
                String t1     = (p.get("typeImageUrl")  instanceof String s) ? s : null;

                String chosen = (avatar != null && !avatar.isBlank()) ? avatar
                        : (prof   != null && !prof.isBlank())   ? prof
                        : (t2     != null && !t2.isBlank())     ? t2
                        : (t1     != null && !t1.isBlank())     ? t1
                        : null;

                if (chosen != null) {
                    if (avatar == null || avatar.isBlank()) { p.put("avatarUrl", chosen); patched = true; }
                    if (prof   == null || prof.isBlank())   { p.put("profileImageUrl", chosen); patched = true; }
                }
            }
            if (patched) {
                ref.update("peers", peersOut).get();
            }

            // 채팅목록 미러 생성/동기화
            Map<String, Object> listCard = mirrorForList(peersOut);
            ref.update("listCard", listCard).get();

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("roomId", roomId);
            resp.put("participants", List.copyOf(participants)); // 숫자 반환
            resp.put("peers", peersOut);
            resp.put("listCard", listCard);
            resp.put("createdAt", createdAtIso);
            return resp;

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /** 탈퇴 사용자를 포함한 모든 방의 peers/listCard를 마스킹 (항상 otherUid 쪽을 갱신) */
    public void markUserLeft(Long userId, String unknownName, String unknownImage) {
        String uid = String.valueOf(userId);
        try {
            CollectionReference col = firestore.collection("chatRooms");
            QuerySnapshot qs = col.whereArrayContains("participants", uid).get().get();

            for (DocumentSnapshot doc : qs.getDocuments()) {
                List<String> participants = readParticipantsAsString(doc);
                if (participants.isEmpty()) continue;

                for (String otherUid : participants) {
                    if (uid.equals(otherUid)) continue;
                    DocumentReference ref = doc.getReference();
                    Map<String, Object> updates = new HashMap<>();
                    // peers (목록/방 모두 동일 반영을 위해 otherUid 아래에 기록)
                    String base = "peers."+otherUid;
                    updates.put(base+".userId", userId);
                    updates.put(base+".name", unknownName);
                    updates.put(base+".department", null);
                    updates.put(base+".typeImageUrl", unknownImage);
                    updates.put(base+".typeImageUrl2", unknownImage);
                    updates.put(base+".typeImageUrl3", unknownImage);
                    updates.put(base+".avatarUrl", unknownImage);
                    updates.put(base+".profileImageUrl", unknownImage);
                    updates.put(base+".status", "LEFT");
                    // listCard 미러
                    String lbase = "listCard."+otherUid;
                    updates.put(lbase+".userId", userId);
                    updates.put(lbase+".name", unknownName);
                    updates.put(lbase+".department", null);
                    updates.put(lbase+".typeImageUrl", unknownImage);
                    updates.put(lbase+".typeImageUrl2", unknownImage);
                    updates.put(lbase+".typeImageUrl3", unknownImage);
                    updates.put(lbase+".avatarUrl", unknownImage);
                    updates.put(lbase+".profileImageUrl", unknownImage);
                    updates.put(lbase+".status", "LEFT");
                    ref.update(updates).get();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /** 프사 변경 전파 – 항상 otherUid 아래에 반영 + listCard 동기화 */
    public void updatePeerAvatar(Long userId, String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return;
        String uid = String.valueOf(userId);
        try {
            CollectionReference col = firestore.collection("chatRooms");
            QuerySnapshot qs = col.whereArrayContains("participants", uid).get().get();

            for (DocumentSnapshot doc : qs.getDocuments()) {
                List<String> participants = readParticipantsAsString(doc);
                if (participants.isEmpty()) continue;

                for (String otherUid : participants) {
                    if (uid.equals(otherUid)) continue;
                    DocumentReference ref = doc.getReference();
                    Map<String, Object> updates = new HashMap<>();
                    String base = "peers."+otherUid;
                    updates.put(base+".avatarUrl", avatarUrl);
                    updates.put(base+".profileImageUrl", avatarUrl);
                    updates.put(base+".typeImageUrl2", avatarUrl);
                    updates.put(base+".typeImageUrl3", avatarUrl);
                    String lbase = "listCard."+otherUid;
                    updates.put(lbase+".avatarUrl", avatarUrl);
                    updates.put(lbase+".profileImageUrl", avatarUrl);
                    updates.put(lbase+".typeImageUrl2", avatarUrl);
                    updates.put(lbase+".typeImageUrl3", avatarUrl);
                    ref.update(updates).get();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /** 이름 변경 전파 – 항상 otherUid 아래에 반영 + listCard 동기화 */
    public void updatePeerName(Long userId, String name) {
        if (name == null || name.isBlank()) return;
        String uid = String.valueOf(userId);
        try {
            CollectionReference col = firestore.collection("chatRooms");
            QuerySnapshot qs = col.whereArrayContains("participants", uid).get().get();

            for (DocumentSnapshot doc : qs.getDocuments()) {
                List<String> participants = readParticipantsAsString(doc);
                if (participants.isEmpty()) continue;

                for (String otherUid : participants) {
                    if (uid.equals(otherUid)) continue;
                    DocumentReference ref = doc.getReference();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("peers."+otherUid+".name", name);
                    updates.put("listCard."+otherUid+".name", name);
                    ref.update(updates).get();
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    // ---- helpers ----

    @SuppressWarnings("unchecked")
    private List<String> readParticipantsAsString(DocumentSnapshot doc) {
        Object raw = doc.get("participants");
        if (!(raw instanceof List<?> list)) return List.of();
        List<String> out = new ArrayList<>(list.size());
        for (Object o : list) {
            if (o == null) continue;
            if (o instanceof String s) out.add(s);
            else if (o instanceof Number n) out.add(String.valueOf(n.longValue()));
            else out.add(String.valueOf(o));
        }
        // 빈값 제거
        out.removeIf(s -> s == null || s.isBlank());
        return out;
    }

    private Map<String, Object> mirrorForList(Map<String, Object> peersOut) {
        Map<String, Object> listCard = new HashMap<>();
        for (Map.Entry<String, Object> e : peersOut.entrySet()) {
            if (e.getValue() instanceof Map<?,?> m) {
                listCard.put(e.getKey(), new HashMap<>(m)); // 얕은 복제
            }
        }
        return listCard;
    }
}
