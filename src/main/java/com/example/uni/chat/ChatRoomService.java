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
     * participants: [uid1, uid2]
     * peers: { "uid1": {...}, "uid2": {...} }  // 각 키는 문자열 UID
     * 반환: { roomId, participants, peers, createdAt(ISO8601 UTC) }
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
                // 최초 생성
                Map<String, Object> data = new LinkedHashMap<>();
                List<Long> sorted = new ArrayList<>(participants);
                Collections.sort(sorted);
                data.put("participants", sorted);
                data.put("peers", peers != null ? peers : Map.of());
                data.put("pairKey", sorted.get(0) + "_" + sorted.get(1));
                data.put("createdAt", FieldValue.serverTimestamp());
                ref.set(data, SetOptions.merge()).get();

                // 서버 타임스탬프 반영 위해 한 번 더 읽기
                snap = ref.get().get();
            }

            // 응답 생성
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("roomId", roomId);
            resp.put("participants", snap.get("participants", List.class));

            @SuppressWarnings("unchecked")
            Map<String, Object> peersRead = (Map<String, Object>) snap.get("peers");
            resp.put("peers", peersRead != null ? peersRead : Map.of());

            Timestamp ts = snap.getTimestamp("createdAt");
            String createdAtIso = (ts != null) ? Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()).toString() : null;
            resp.put("createdAt", createdAtIso);
            return resp;

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ie);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
