package com.example.uni.chat.service;

import com.example.uni.chat.domain.ChatMessage;
import com.example.uni.chat.domain.ChatRoom;
import com.example.uni.chat.domain.MessageType;
import com.example.uni.chat.repo.ChatMessageRepository;
import com.example.uni.chat.repo.ChatRoomRepository;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;

    @Transactional
    public ChatRoom createOrReuseRoom(UUID meId, UUID peerId){
        User me = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        return roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(peer, me))
                .orElseGet(() -> roomRepo.save(ChatRoom.builder()
                        .userA(me).userB(peer)
                        .anonymousNameA("별"+me.getId().toString().substring(0,4))
                        .anonymousNameB("별"+peer.getId().toString().substring(0,4))
                        .accepted(true)
                        .build()));
    }

    @Transactional
    public void sendSignal(UUID roomId, UUID fromUserId, String action){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User from = userRepo.findById(fromUserId).orElse(null);
        msgRepo.save(ChatMessage.builder()
                .room(room).sender(from).type(MessageType.SYSTEM_SIGNAL)
                .content("SIGNAL:"+action).build());
        if ("ACCEPT".equalsIgnoreCase(action)) room.setAccepted(true);
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> listMyRooms(UUID meId, String cursor, int size){
        User me = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        LocalDateTime before = parseCursor(cursor);
        List<ChatRoom> rooms = roomRepo.findByUserAOrUserB(me, me);
        List<Map<String,Object>> out = new ArrayList<>();

        for (ChatRoom r : rooms) {
            ChatMessage last = msgRepo.findTop1ByRoomOrderByCreatedAtDesc(r);
            if (last == null) continue;
            if (before != null && (last.getCreatedAt() == null || !last.getCreatedAt().isBefore(before))) continue;

            UUID peer = r.getUserA().getId().equals(meId) ? r.getUserB().getId() : r.getUserA().getId();
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("roomId", r.getId());
            row.put("peerId", peer);
            row.put("accepted", r.isAccepted());
            row.put("lastMessageType", last.getType().name());
            row.put("lastMessage", last.getContent());
            row.put("lastMessageAt", last.getCreatedAt());
            out.add(row);
        }

        out.sort((a,b) -> {
            LocalDateTime ba = (LocalDateTime) b.get("lastMessageAt");
            LocalDateTime aa = (LocalDateTime) a.get("lastMessageAt");
            if (ba == null && aa == null) return 0;
            if (ba == null) return 1;
            if (aa == null) return -1;
            return ba.compareTo(aa);
        });
        if (out.size() > size) return out.subList(0, size);
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> listMessages(UUID roomId, UUID meId, String cursor, int size){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!room.getUserA().getId().equals(meId) && !room.getUserB().getId().equals(meId)) {
            throw new ApiException(ErrorCode.NOT_FOUND);
        }

        List<ChatMessage> msgs;
        LocalDateTime after = parseCursor(cursor);
        if (after != null) {
            msgs = msgRepo.findByRoomAndCreatedAtAfterOrderByCreatedAtAsc(room, after);
            if (msgs.size() > size) msgs = msgs.subList(0, size);
        } else {
            msgs = msgRepo.findByRoomOrderByCreatedAtDesc(room, PageRequest.of(0, size));
            Collections.reverse(msgs);
        }

        List<Map<String,Object>> out = new ArrayList<>();
        for (ChatMessage m : msgs) {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id", m.getId());
            row.put("senderId", m.getSender()==null ? null : m.getSender().getId());
            row.put("type", m.getType().name());
            row.put("content", m.getContent());
            row.put("createdAt", m.getCreatedAt());
            out.add(row);
        }
        return out;
    }

    private LocalDateTime parseCursor(String cursor){
        if (cursor == null || cursor.isBlank()) return null;
        try { return LocalDateTime.parse(cursor); } catch (DateTimeParseException e) { return null; }
    }
}