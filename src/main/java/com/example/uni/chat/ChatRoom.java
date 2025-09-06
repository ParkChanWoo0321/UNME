// src/main/java/com/example/uni/chat/domain/ChatRoom.java
package com.example.uni.chat;

import com.example.uni.common.domain.BaseTimeEntity;
import com.example.uni.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "chat_room",
        uniqueConstraints = @UniqueConstraint(name="uk_room_pair", columnNames = {"user_a_id","user_b_id"}))
public class ChatRoom extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) @JoinColumn(name="user_a_id", nullable=false)
    private User userA;

    @ManyToOne(optional=false) @JoinColumn(name="user_b_id", nullable=false)
    private User userB;

    private String anonymousNameA;
    private String anonymousNameB;

    private boolean accepted;
}
