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
public class ChatMessage extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) private ChatRoom room;
    @ManyToOne private User sender;
    @Enumerated(EnumType.STRING) private MessageType type;

    @Column(length=1000) private String content;
}
