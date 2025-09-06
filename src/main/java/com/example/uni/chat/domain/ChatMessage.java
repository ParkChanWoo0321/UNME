// src/main/java/com/example/uni/chat/domain/ChatMessage.java
package com.example.uni.chat.domain;

import com.example.uni.common.domain.BaseTimeEntity;
import com.example.uni.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@Entity
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "chat_message",
        indexes = {
                @Index(name="idx_msg_room_created", columnList = "room_id,createdAt"),
                @Index(name="idx_msg_created",     columnList = "createdAt")
        })
public class ChatMessage extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) @JoinColumn(name="room_id", nullable=false)
    private ChatRoom room;

    @ManyToOne @JoinColumn(name="sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(length=1000)
    private String content;
}
