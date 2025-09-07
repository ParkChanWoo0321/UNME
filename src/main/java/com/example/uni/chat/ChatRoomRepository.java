package com.example.uni.chat;

import com.example.uni.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByUserAAndUserB(User a, User b);
    Optional<ChatRoom> findByUserBAndUserA(User b, User a);
    @Query("""
           select r
             from ChatRoom r
            where r.accepted = true
              and (r.userA = :u or r.userB = :u)
            order by r.updatedAt desc
           """)
    List<ChatRoom> findAllForUserOrderByUpdatedAtDesc(@Param("u") User u);
}
