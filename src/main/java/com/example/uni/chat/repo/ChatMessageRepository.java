package com.example.uni.chat.repo;

import com.example.uni.chat.domain.ChatMessage;
import com.example.uni.chat.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    // 방의 after(시간) 이후 메시지 (오래→최신)
    List<ChatMessage> findByRoomAndCreatedAtAfterOrderByCreatedAtAsc(ChatRoom room, LocalDateTime after);

    // 방의 최신 1개 (마지막 메시지)
    ChatMessage findTop1ByRoomOrderByCreatedAtDesc(ChatRoom room);

    // 방의 최신 N개 (내림차순) - Pageable 사용
    List<ChatMessage> findByRoomOrderByCreatedAtDesc(ChatRoom room, Pageable pageable);
}
