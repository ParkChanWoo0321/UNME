package com.example.uni.chat.service;

import com.example.uni.chat.domain.ChatMessage;
import com.example.uni.chat.domain.ChatRoom;
import com.example.uni.chat.domain.MessageType;
import com.example.uni.chat.repo.ChatMessageRepository;
import com.example.uni.chat.repo.ChatRoomRepository;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.common.realtime.RealtimeNotifier;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final RealtimeNotifier notifier;

    @Transactional
    public ChatRoom createOrReuseRoom(UUID meId, UUID peerId){
        User me   = userRepo.findById(meId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User peer = userRepo.findById(peerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        Optional<ChatRoom> legacy = roomRepo.findByUserAAndUserB(me, peer)
                .or(() -> roomRepo.findByUserBAndUserA(me, peer));
        if (legacy.isPresent()) return legacy.get();

        User a = me.getId().compareTo(peer.getId()) <= 0 ? me : peer;
        User b = (a == me) ? peer : me;

        Optional<ChatRoom> found = roomRepo.findByUserAAndUserB(a, b);
        if (found.isPresent()) return found.get();

        try {
            return roomRepo.save(ChatRoom.builder()
                    .userA(a).userB(b)
                    .anonymousNameA("별"+a.getId().toString().substring(0,4))
                    .anonymousNameB("별"+b.getId().toString().substring(0,4))
                    .accepted(true)
                    .build());
        } catch (DataIntegrityViolationException e) {
            return roomRepo.findByUserAAndUserB(a, b).orElseThrow(() -> e);
        }
    }

    @Transactional
    public UUID sendText(UUID roomId, UUID senderId, String content){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!room.getUserA().getId().equals(senderId) && !room.getUserB().getId().equals(senderId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        String body = (content == null) ? "" : content.trim();
        if (body.isEmpty() || body.length() > 1000) throw new ApiException(ErrorCode.VALIDATION_ERROR);

        User sender = userRepo.findById(senderId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        ChatMessage msg = msgRepo.save(ChatMessage.builder()
                .room(room).sender(sender).type(MessageType.TEXT).content(body).build());

        // 미읽음 증가
        if (room.getUserA().getId().equals(senderId)) {
            room.setUnreadCountB((room.getUnreadCountB()==null?0:room.getUnreadCountB()) + 1);
        } else {
            room.setUnreadCountA((room.getUnreadCountA()==null?0:room.getUnreadCountA()) + 1);
        }

        UUID a = room.getUserA().getId();
        UUID b = room.getUserB().getId();
        UUID peer = a.equals(senderId) ? b : a;

        Map<String,Object> payload = Map.of(
                "id", msg.getId(),
                "roomId", roomId,
                "senderId", senderId,
                "type", msg.getType().name(),
                "content", body,
                "createdAt", msg.getCreatedAt()
        );

        notifier.toUser(peer,     RealtimeNotifier.qChat(roomId), payload);
        notifier.toUser(senderId, RealtimeNotifier.qChat(roomId), payload);

        Map<String,Object> summary = Map.of(
                "roomId", roomId,
                "lastMessageType", msg.getType().name(),
                "lastMessage", body,
                "lastMessageAt", msg.getCreatedAt()
        );
        notifier.toUser(peer,     RealtimeNotifier.Q_CHAT_LIST, summary);
        notifier.toUser(senderId, RealtimeNotifier.Q_CHAT_LIST, summary);

        return msg.getId();
    }

    /** 시스템 신호 메시지 */
    @Transactional
    public void sendSignal(UUID roomId, UUID fromUserId, String action){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        User from = (fromUserId == null) ? null : userRepo.findById(fromUserId).orElse(null);
        msgRepo.save(ChatMessage.builder()
                .room(room).sender(from).type(MessageType.SYSTEM_SIGNAL)
                .content("SIGNAL:"+action).build());
        if ("ACCEPT".equalsIgnoreCase(action)) room.setAccepted(true);
    }

    /** 읽음 처리 */
    @Transactional
    public void markRead(UUID roomId, UUID meId){
        ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        if (!room.getUserA().getId().equals(meId) && !room.getUserB().getId().equals(meId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
        if (room.getUserA().getId().equals(meId)) room.setUnreadCountA(0);
        else room.setUnreadCountB(0);
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
            Integer unread = r.getUserA().getId().equals(meId) ? r.getUnreadCountA() : r.getUnreadCountB();

            Map<String,Object> row = new LinkedHashMap<>();
            row.put("roomId", r.getId());
            row.put("peerId", peer);
            row.put("accepted", r.isAccepted());
            row.put("unreadCount", unread == null ? 0 : unread);
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
