package com.example.uni.chat;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
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
                String pairKey = sortedNum.get(0) + "_" + sortedNum.get(1);

                Map<String, Object> data = new LinkedHashMap<>();
                data.put("participants", sortedNum);
                data.put("peers", peers != null ? peers : Map.of());
                data.put("pairKey", pairKey);
                data.put("createdAt", FieldValue.serverTimestamp());
                ref.set(data, SetOptions.merge()).get();

                snap = ref.get().get();
            } else {
                Object p = snap.get("participants");
                if (p instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String) {
                    List<Long> migrated = new ArrayList<>();
                    for (Object v : list) migrated.add(Long.valueOf(String.valueOf(v)));
                    Map<String, Object> upd = new HashMap<>();
                    upd.put("participants", migrated);
                    String pairKey = migrated.get(0) + "_" + migrated.get(1);
                    upd.put("pairKey", pairKey);
                    ref.update(upd).get();
                    snap = ref.get().get();
                }
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
            QuerySnapshot qsNum = col.whereArrayContains("participants", userId).get().get();
            QuerySnapshot qsStr = col.whereArrayContains("participants", uid).get().get();

            Map<String, DocumentSnapshot> docs = new LinkedHashMap<>();
            for (DocumentSnapshot d : qsNum.getDocuments()) docs.put(d.getId(), d);
            for (DocumentSnapshot d : qsStr.getDocuments()) docs.put(d.getId(), d);

            for (DocumentSnapshot doc : docs.values()) {
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
