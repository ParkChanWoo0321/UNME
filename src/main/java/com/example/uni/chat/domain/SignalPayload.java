package com.example.uni.chat.domain;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SignalPayload {
    private String fromUserId;
    private String action; // SEND | ACCEPT | DECLINE
}
