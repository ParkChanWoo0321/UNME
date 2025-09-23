package com.example.uni.push;

import com.example.uni.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "push_subscriptions", indexes = {
        @Index(name = "idx_push_sub_user", columnList = "user_id", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 1024)
    private String endpoint;

    @Column(nullable = false, length = 255)
    private String p256dh;

    @Column(nullable = false, length = 255)
    private String auth;
}
