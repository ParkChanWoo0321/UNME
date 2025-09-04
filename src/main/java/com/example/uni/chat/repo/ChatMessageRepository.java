package com.example.uni.chat.repo;

import com.example.uni.chat.domain.ChatMessage;
import com.example.uni.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findTop50ByRoomOrderByCreatedAtDesc(ChatRoom room);
}
