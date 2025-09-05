package com.example.uni.chat.repo;

import com.example.uni.chat.domain.ChatRoom;
import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByUserAAndUserB(User a, User b);
    Optional<ChatRoom> findByUserBAndUserA(User b, User a);
    List<ChatRoom> findByUserAOrUserB(User a, User b);
}
