package com.example.uni.chat;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
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
                data.put("participantsNum", sortedNum);
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

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("roomId", roomId);
            resp.put("participants", List.copyOf(participants));
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
