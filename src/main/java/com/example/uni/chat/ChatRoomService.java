// com/example/uni/chat/ChatRoomService.java
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
     * participants: [uid1, uid2] (숫자)  ← 응답은 숫자로 반환
     * peers: { "uid1": {...}, "uid2": {...} }  // 키는 문자열 UID
     * 문서 저장 시 participants는 문자열 배열로 저장(Security Rules용)
     * 반환: { roomId, participants(숫자), peers, createdAt(ISO8601 UTC) }
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
                // Firestore에는 문자열 UID로 저장
                List<Long> sortedNum = new ArrayList<>(participants);
                Collections.sort(sortedNum);
                List<String> participantsStr = List.of(
                        String.valueOf(sortedNum.get(0)),
                        String.valueOf(sortedNum.get(1))
                );

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("participants", participantsStr);                 // 문자열 저장
                data.put("peers", peers != null ? peers : Map.of());
                data.put("pairKey", participantsStr.get(0) + "_" + participantsStr.get(1));
                data.put("createdAt", FieldValue.serverTimestamp());
                ref.set(data, SetOptions.merge()).get();

                // 서버 타임스탬프 반영 위해 재조회
                snap = ref.get().get();
            }

            // createdAt
            Timestamp ts = snap.getTimestamp("createdAt");
            String createdAtIso = (ts != null)
                    ? Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()).toString()
                    : null;

            // peers (문서에 있으면 문서 기준, 없으면 파라미터)
            @SuppressWarnings("unchecked")
            Map<String, Object> peersOut = (Map<String, Object>) snap.get("peers");
            if (peersOut == null) peersOut = (peers != null ? peers : Map.of());

            // 응답: participants는 숫자 그대로 반환
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("roomId", roomId);
            resp.put("participants", List.copyOf(participants)); // 숫자
            resp.put("peers", peersOut);
            resp.put("createdAt", createdAtIso);
            return resp;

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 탈퇴 사용자를 포함한 모든 방의 peers를 마스킹하고 상태를 LEFT로 표시
     */
    public void markUserLeft(Long userId, String unknownName, String unknownImage) {
        String uid = String.valueOf(userId);
        try {
            CollectionReference col = firestore.collection("chatRooms");
            Query query = col.whereArrayContains("participants", uid);
            QuerySnapshot qs = query.get().get();

            for (DocumentSnapshot doc : qs.getDocuments()) {
                DocumentReference ref = doc.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("peers."+uid+".userId", userId);
                updates.put("peers."+uid+".name", unknownName);
                updates.put("peers."+uid+".department", null);
                updates.put("peers."+uid+".typeImageUrl", unknownImage);
                updates.put("peers."+uid+".typeImageUrl2", unknownImage);
                updates.put("peers."+uid+".status", "LEFT");
                ref.update(updates).get();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
