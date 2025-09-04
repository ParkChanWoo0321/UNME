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
public class ChatRoom extends BaseTimeEntity {
    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional=false) private User userA;
    @ManyToOne(optional=false) private User userB;

    private String anonymousNameA;
    private String anonymousNameB;

    private boolean accepted;

    private Integer unreadCountA = 0;
    private Integer unreadCountB = 0;
}
